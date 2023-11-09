package com.xoso.agency.service.impl;

import com.xoso.agency.data.AgencyState;
import com.xoso.agency.data.AgencyUser;
import com.xoso.agency.model.AgencyRequest;
import com.xoso.agency.model.AgencyStatus;
import com.xoso.agency.repository.AgencyRequestRepository;
import com.xoso.agency.service.AgencyService;
import com.xoso.agency.wsdto.AgencyCreateWsDTO;
import com.xoso.agency.wsdto.AgencyRequestWsDTO;
import com.xoso.bank.model.Bank;
import com.xoso.bank.model.ClientBankAccount;
import com.xoso.bank.repository.BankRepository;
import com.xoso.bank.repository.ClientBankAccountRepository;
import com.xoso.captcha.service.CaptchaService;
import com.xoso.infrastructure.core.data.SearchParameters;
import com.xoso.infrastructure.core.exception.BadRequestException;
import com.xoso.infrastructure.core.exception.ExceptionCode;
import com.xoso.infrastructure.core.service.JdbcSupport;
import com.xoso.infrastructure.core.service.Page;
import com.xoso.infrastructure.core.service.PaginationHelper;
import com.xoso.infrastructure.security.service.AuthenticationService;
import com.xoso.user.model.AppUser;
import com.xoso.user.service.UserReadService;
import com.xoso.user.service.UserWriteService;
import com.xoso.wallet.data.WalletData;
import com.xoso.wallet.model.Wallet;
import com.xoso.wallet.service.WalletService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestParam;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

@Service
public class AgencyServiceImpl implements AgencyService {
    @Autowired
    WalletService walletService;
    @Autowired
    UserWriteService userWriteService;
    @Autowired
    UserReadService userReadService;
    @Autowired
    AgencyRequestRepository agencyRequestRepository;
    @Autowired
    AuthenticationService authenticationService;
    @Autowired
    BankRepository bankRepository;
    @Autowired
    ClientBankAccountRepository clientBankAccountRepository;
    @Autowired
    private PaginationHelper paginationHelper;
    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private CaptchaService captchaService;
    @Override
    public AgencyState register(AgencyCreateWsDTO request) {
        //validate captcha
        captchaService.validateCaptcha(request.getCaptchaId(), request.getCaptchaValue());
        AppUser user = authenticationService.authenticatedUser();
        if(user.getAgencyStatus() == AgencyStatus.APPROVED)
            throw new BadRequestException(ExceptionCode.AGENCY_REQUEST_ALREADY_APPROVED);
        if(user.getAgencyStatus() == AgencyStatus.PROCESSING)
            throw new BadRequestException(ExceptionCode.AGENCY_REQUEST_IS_PROCESSING);
        //validate
        //kiem tra bankId co ton tai khong
        Bank bank = bankRepository.findById(request.getBankId()).orElseThrow(()->new BadRequestException(ExceptionCode.BANK_NOT_FOUND));
        //kiem tra bank & bankNumber da thuoc tai khoan nao khac hay chua
        ClientBankAccount clientBanks = clientBankAccountRepository.findByBankAndAccountNumber(bank,request.getAccountNumber());
        if(clientBanks != null && clientBanks.getUser().getId() != user.getId())
            throw new BadRequestException(ExceptionCode.BANK_ACCOUNT_ALREADY_EXISTED);
        //xem da co request nao chua
        AgencyRequest newRequest = AgencyRequest.builder()
                .userId(user.getId())
                .fullName(request.getFullName())
                .email(request.getEmail())
                .bankId(request.getBankId())
                .accountName(request.getAccountName())
                .accountNumber(request.getAccountNumber())
                .status(AgencyStatus.PROCESSING)
                .build();

        AgencyRequest oldRequest = agencyRequestRepository.findByUserId(user.getId());
        if(oldRequest != null)
            newRequest.setId(oldRequest.getId());
        agencyRequestRepository.saveAndFlush(newRequest);
        //cap nhat user status
        user.setAgencyStatus(AgencyStatus.PROCESSING);
        userWriteService.registerAgency(user.getId());
        return AgencyState.builder().state(AgencyStatus.PROCESSING).build();
    }

    @Override
    public AgencyState getState() {
        AppUser user = authenticationService.authenticatedUser();
        String reason = "";
        if(user.getAgencyStatus() == AgencyStatus.REJECTED){
            AgencyRequest request = agencyRequestRepository.findByUserId(user.getId());
            if(request != null)
                reason = request.getNote();
        }

        return AgencyState.builder().state(user.getAgencyStatus()).reason(reason).build();
    }

    @Override
    public AgencyState approve(Long id) {
        AgencyRequest request = agencyRequestRepository.findById(id).orElseThrow(()->new BadRequestException(ExceptionCode.AGENCY_REQUEST_NOT_EXISTED));

        Bank bank = bankRepository.findById(request.getBankId()).orElseThrow(()->new BadRequestException(ExceptionCode.BANK_NOT_FOUND));
        //kiem tra bank & bankNumber da thuoc tai khoan nao khac hay chua
        ClientBankAccount clientBanks = clientBankAccountRepository.findByBankAndAccountNumber(bank,request.getAccountNumber());
        if(clientBanks != null && clientBanks.getUser().getId() != request.getUserId())
            throw new BadRequestException(ExceptionCode.BANK_ACCOUNT_ALREADY_EXISTED);
        request.setStatus(AgencyStatus.APPROVED);
        agencyRequestRepository.saveAndFlush(request);

        userWriteService.approveAgency(request,bank);
        return AgencyState.builder().state(AgencyStatus.APPROVED).build();
    }

    @Override
    public AgencyState reject(Long id, String reason) {
        AgencyRequest request = agencyRequestRepository.findById(id).orElseThrow(()->new BadRequestException(ExceptionCode.AGENCY_REQUEST_NOT_EXISTED));
        request.setStatus(AgencyStatus.REJECTED);
        request.setNote(reason);
        agencyRequestRepository.saveAndFlush(request);
        userWriteService.rejectAgency(request);
        return AgencyState.builder().state(AgencyStatus.REJECTED).build();
    }

    @Override
    public Page<AgencyRequestWsDTO> retrieveAllAgencyRequest(SearchParameters searchParameters) {
        var mapper = new AgencyRequestMapper();
        final StringBuilder sqlBuilder = new StringBuilder(200);
        var paramList = new ArrayList<Object>();

        sqlBuilder.append("select ");
        sqlBuilder.append(mapper.schema);

        var extraCriteria = " where 1=1 ";
        var searchValue = searchParameters.getSearchValue();
        if (StringUtils.isNotBlank(searchValue)) {
            extraCriteria += " and " +
                    "(ar.full_name like ? " +
                    "or ar.email like ?) ";
            var userInfo = "%" + searchValue + "%";
            paramList.add(userInfo);
            paramList.add(userInfo);
        }

        if (searchParameters.getUserId() != null) {
            extraCriteria += " and ar.user_id = ? ";
            paramList.add(searchParameters.getUserId());
        }

        var statusInt = searchParameters.getStatusInt();
        if (statusInt != null) {
            extraCriteria += " and ar.status = ? ";
            paramList.add(statusInt);
        }

        sqlBuilder.append(extraCriteria);
        if (StringUtils.isNotBlank(searchParameters.getOrderBy())) {
            sqlBuilder.append(" order by ").append(searchParameters.getOrderBy());
        } else {
            sqlBuilder.append(" order by ar.full_name DESC ");
        }

        var sqlBuilderNoPaging = sqlBuilder.toString();
        if (searchParameters.isLimited()) {
            sqlBuilder.append(" limit ").append(searchParameters.getLimit());
            if (searchParameters.isOffset()) {
                sqlBuilder.append(" offset ").append(searchParameters.getLimit()*searchParameters.getOffset());
            }
        } else {
            sqlBuilder.append(" limit 1000 offset 0 ");
        }

        return this.paginationHelper.fetchPage(this.jdbcTemplate, sqlBuilder.toString(), sqlBuilderNoPaging,
                paramList.toArray(), mapper);
    }

    @Override
    public AgencyRequestWsDTO retrieveOneAgencyRequest(Long id) {
        var mapper = new AgencyRequestMapper();
        final String sql = "select " + mapper.schema()
                + " where ar.id = ?";
        return this.jdbcTemplate.queryForObject(sql, mapper, new Object[] { id });
    }

    private static final class AgencyRequestMapper implements RowMapper<AgencyRequestWsDTO> {
        private final String schema;
        public AgencyRequestMapper() {
            final var builder = new StringBuilder(400)
                    .append("ar.id as id, ar.user_id as userId, ar.full_name as fullName, ar.email as email,")
                    .append("au.agency_level as agencyLevel, au.mobile_no as mobileNo, ")
                    .append("b.id as bankId, b.name as bankName, ar.bank_account_name as bankAccountName, ar.bank_account_number as bankAccountNumber, ar.note as note, ar.status as status ")
                    .append("from agency_request ar ")
                    .append("join bank b on b.id = ar.bank_id ")
                    .append("join app_user au on au.id = ar.user_id ");
            this.schema = builder.toString();
        }
        public String schema() {
            return this.schema;
        }
        @Override
        public AgencyRequestWsDTO mapRow(ResultSet rs, int rowNum) throws SQLException {
            var id = JdbcSupport.getLong(rs, "id");
            var userId = JdbcSupport.getLong(rs, "userId");
            var fullName = rs.getString("fullName");
            var email = rs.getString("email");
            var bankId = JdbcSupport.getLong(rs, "bankId");
            var bankName = rs.getString("bankName");
            var bankAccountName = rs.getString("bankAccountName");
            var bankAccountNumber = rs.getString("bankAccountNumber");

            var agencyLevel = rs.getInt("agencyLevel");
            var mobileNo = rs.getString("mobileNo");

            var note = rs.getString("note");
            var statusInt = rs.getInt("status");
            var status = "";
            switch (statusInt) {
                case 1:
                    status = "PROCESSING";
                    break;
                case 2:
                    status = "APPROVED";
                    break;
                case -1:
                    status = "REJECTED";
                    break;
                default:
                    break;
            }
            return new AgencyRequestWsDTO(id, userId, fullName, email, bankId, bankName, bankAccountName, bankAccountNumber, note, statusInt, status, agencyLevel, mobileNo);
        }
    }

    @Override
    public org.springframework.data.domain.Page<AgencyUser> getUserList(int pageNumber, int pageSize, String searchValue) {
        Pageable pageable = PageRequest.of(pageNumber-1, pageSize);
        AppUser user = authenticationService.authenticatedUser();
        return userReadService.retrieveAllAgencyUser(user.getId(),pageable, searchValue).map(usr->{
            WalletData usrWallet = walletService.getWalletByUsername(usr.getUsername());

            return AgencyUser.builder()
                    .userName(usr.getUsername())
                    .registerDate(usr.getCreatedDate())
                    .totalBet(usrWallet.getTotalBet().doubleValue())
                    .totalWin(usrWallet.getTotalWin().doubleValue())
                    .totalDeposit(usrWallet.getTotalDeposit().doubleValue())
                    .totalWithdraw(usrWallet.getTotalWithdraw().doubleValue())
                    .balance(usrWallet.getBalance().doubleValue())
                    .build();
        });
    }
}
