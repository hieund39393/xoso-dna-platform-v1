package com.xoso.lottery.service.impl;

import com.xoso.infrastructure.core.data.SearchParameters;
import com.xoso.infrastructure.core.exception.BadRequestException;
import com.xoso.infrastructure.core.exception.ExceptionCode;
import com.xoso.infrastructure.core.service.JdbcSupport;
import com.xoso.infrastructure.core.service.PaginationHelper;
import com.xoso.infrastructure.security.service.AuthenticationService;
import com.xoso.lottery.data.LotteryTicketData;
import com.xoso.lottery.model.*;
import com.xoso.lottery.repository.LotteryModeRepository;
import com.xoso.lottery.repository.LotteryRepository;
import com.xoso.lottery.repository.LotterySessionRepository;
import com.xoso.lottery.repository.LotteryTicketRepository;
import com.xoso.lottery.service.LotteryTicketService;
import com.xoso.lottery.wsdto.LotteryTicketSearchWsDTO;
import com.xoso.lottery.wsdto.LotteryTicketWsDTO;
import com.xoso.lottery.wsdto.TicketWsDTO;
import com.xoso.user.model.AppUser;
import com.xoso.wallet.service.WalletService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class LotteryTicketServiceImpl implements LotteryTicketService {
    @Autowired
    LotteryRepository lotteryRepository;
    @Autowired
    LotterySessionRepository lotterySessionRepository;
    @Autowired
    LotteryModeRepository lotteryModeRepository;
//    @Autowired
//    WalletRepository walletRepository;
    @Autowired
    WalletService walletService;
    @Autowired
    LotteryTicketRepository lotteryTicketRepository;
    @Autowired
    AuthenticationService authenticationService;

    @Autowired
    private PaginationHelper paginationHelper;
    @Autowired
    private JdbcTemplate jdbcTemplate;
    @Override
    @Transactional
    public List<LotteryTicketData> buyTicket(LotteryTicketWsDTO ticket) {
        //lay thong tin user
        AppUser user = authenticationService.authenticatedUser();
        //lay thong tin lottery
        Lottery lottery = lotteryRepository.findById(ticket.getLotteryId()).get();
        long currTime = System.currentTimeMillis();
        if(lottery == null || !lottery.isActive())
            throw new BadRequestException(ExceptionCode.LOTTERY_INVALID);
        //lay thong tin session hien tai
        LotterySession latestSession = lotterySessionRepository.findFirstByLotteryIdOrderByIdDesc(lottery.getId());
        //validate
        if(latestSession == null || latestSession.getStatus() != SessionStatus.WAITING){
            throw new BadRequestException(ExceptionCode.BUY_TICKET_EXPIRED_TIME);
        }

        if(lottery.isSXB() && currTime>(latestSession.getStartTime() - 5*60*1000)){
            //xs bip can mua truoc khi quay 5 phut
            throw new BadRequestException(ExceptionCode.BUY_TICKET_EXPIRED_TIME);
        }

        if(lottery.isXSCT() && currTime>(latestSession.getStartTime() - 35*60*1000)){
            //xs chinh thong can mua truoc khi quay 35 phut
            throw new BadRequestException(ExceptionCode.BUY_TICKET_EXPIRED_TIME);
        }
        

        //validate mode
        List<String> supportedMode = Arrays.asList(lottery.getModes().split("-"));

        //tinh toan va luu vao db
        //tinh tong so tien can mua
        List<LotteryMode> modes = lotteryModeRepository.findAll();
        long totalPrice = 0;
        List<LotteryTicket> lotteryTickets = new ArrayList<>();
        for(TicketWsDTO t: ticket.getTickets()){
            if(t.getQuantity() <=0)
                throw new BadRequestException(ExceptionCode.LOTTERY_TICKET_INVALID);
            ModeCode mode = t.getLotteryMode();
            if(!supportedMode.contains(mode.getCode()))
                throw new BadRequestException(ExceptionCode.LOTTERY_MODE_INVAILID);
            LotteryMode lotteryMode = modes.stream()
                    .filter(m->m.getCode() == mode)
                    .findFirst().orElse(null);
            if(lotteryMode == null)
                throw new BadRequestException(ExceptionCode.LOTTERY_MODE_INVAILID);
            totalPrice = totalPrice + lotteryMode.getPrice()*t.getQuantity();

            List<String> ticketsNumberStr =Arrays.asList(t.getNumber().split("-"));
            ArrayList<Integer> ticketsNumber = new ArrayList<>();
            try {
                for (String number : ticketsNumberStr) {
                    int nb = Integer.parseInt(number);
                    ticketsNumber.add(nb);
                }
            }catch (Exception e){
                throw new BadRequestException(ExceptionCode.LOTTERY_TICKET_INVALID);
            }
            Collections.sort(ticketsNumber);
            StringBuilder resultBuilder = new StringBuilder();
            for (int i = 0; i < ticketsNumber.size(); i++) {
                resultBuilder.append(String.format("%02d", ticketsNumber.get(i)));
                if (i < ticketsNumber.size() - 1) {
                    resultBuilder.append("-");
                }
            }
            LotteryTicket tk = LotteryTicket.builder()
                    .userId(user.getId())
                    .session(latestSession)
                    .code(mode)
                    .numbers(resultBuilder.toString())
                    .quantity(t.getQuantity())
                    .price(lotteryMode.getPrice()*t.getQuantity())
                    .prizeMoney(lotteryMode.getPrizeMoney()*t.getQuantity())
                    .build();
            tk.setCreatedBy(user.getUsername());
            tk.setCreatedDate(LocalDateTime.now());
            lotteryTickets.add(tk);
        }
//        List<Wallet> wallet = walletRepository.findByUserId(user.getId());
//        if(wallet.isEmpty() || wallet.get(0).getBalance().doubleValue()< totalPrice)
//            throw new BadRequestException("Wallet's balance does not enough");
        //tru tien user
        walletService.updateBalanceOfUserByTicket(user.getId(), (totalPrice*-1));

        //tao cac ticket & insert vao db
        lotteryTickets.forEach(tk->lotteryTicketRepository.save(tk));
        //return ve list cac ticket da mua
        List<LotteryTicket> tickets = lotteryTicketRepository.getPurchasedTicketsBySessionId(user.getId(), latestSession.getId());
        return tickets.stream().map(LotteryTicketData::fromEntity).collect(Collectors.toList());
    }

    @Override
    public List<LotteryTicketData> getPurchasedTicketBySessionId(long sessionId) {
        //lay thong tin user
        AppUser user = authenticationService.authenticatedUser();
        List<LotteryTicket> tickets = lotteryTicketRepository.getPurchasedTicketsBySessionId(user.getId(), sessionId);
        return tickets.stream().map(LotteryTicketData::fromEntity).collect(Collectors.toList());
    }

    @Override
    public Page<LotteryTicketData> queryTicket(long lotteryId, int status, Pageable pageable) {
        AppUser user = authenticationService.authenticatedUser();
        if(status == 2){
            //lay tat ca ticket voi cac trang thai
            Page<LotteryTicket> tickets = lotteryTicketRepository.getTicketByLotteryId(user.getId(), lotteryId, pageable);
            return tickets.map(LotteryTicketData::fromEntity);
        }else{
            Page<LotteryTicket> tickets = lotteryTicketRepository.getTicketByLotteryIdAndStatus(user.getId(), lotteryId,status, pageable);
            return tickets.map(LotteryTicketData::fromEntity);
        }

    }

    @Override
    public com.xoso.infrastructure.core.service.Page<LotteryTicketSearchWsDTO> retrieveAllLotteryTicket(SearchParameters searchParameters) {
        var lotteryTicketMapper = new LotteryTicketMapper();
        var paramList = new ArrayList<Object>();
        final var sqlBuilder = new StringBuilder(200);
        sqlBuilder.append("select ");
        sqlBuilder.append(lotteryTicketMapper.schema);
        String extraCriteria = " where 1=1 ";
        var searchValue = searchParameters.getSearchValue();

        if (StringUtils.isNotBlank(searchValue)) {
            extraCriteria += " and " +
                    "(au.username like ? " +
                    "or au.full_name like ? " +
                    "or au.mobile_no like ?) ";
            var userInfo = "%" + searchValue + "%";
            paramList.add(userInfo);
            paramList.add(userInfo);
            paramList.add(userInfo);
        }
        var lotteryCode = searchParameters.getLotteryCode();
        if (StringUtils.isNotBlank(lotteryCode)) {
            extraCriteria += " and l.code = ? ";
            paramList.add(lotteryCode);
        }

        var modeCodes = searchParameters.getModeCodes();
        if (!CollectionUtils.isEmpty(modeCodes)) {
            StringBuilder sqlMode = new StringBuilder("(");
            var index = 0;
            for (String mode : modeCodes) {
                if (index == modeCodes.size() - 1) {
                    sqlMode.append(String.format("%s%s%s", "'", mode, "'"));
                } else {
                    sqlMode.append(String.format("%s%s%s%s", "'", mode, "'", ","));
                }
                index++;
            }
            sqlMode.append(")");
            extraCriteria += " and lt.code in " + sqlMode;
        }

        var fromDate = searchParameters.getFromDate();
        var toDate = searchParameters.getToDate();

        String fromDateStr = null;
        String toDateStr = null;

        final DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        if (fromDate != null) {
            fromDateStr = df.format(fromDate);
            extraCriteria += " and lt.created_date >=  '" + fromDateStr + "'";
        }
        if (toDate != null) {
            toDateStr = df.format(toDate);
            extraCriteria += " and lt.created_date < '" + toDateStr + "'";
        }

        sqlBuilder.append(extraCriteria);
        sqlBuilder.append(" order by lt.created_date DESC ");
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
                paramList.toArray(), lotteryTicketMapper);
    }

    private static final class LotteryTicketMapper implements RowMapper<LotteryTicketSearchWsDTO> {
        private final String schema;
        public LotteryTicketMapper() {
            final StringBuilder sqlQuery = new StringBuilder()
                    .append("au.id as userId, au.username as username, au.full_name as fullName, au.mobile_no as mobileNo, ")
                    .append("lt.id as id, lt.code as code, lt.numbers as numbers, lt.quantity as quantity, lt.price as price, ")
                    .append("lt.prize_money as prizeMoney, lt.win as win, lt.status as status, lt.created_date as createdDate, ")
                    .append("l.name as lotteryName, l.code as lotteryCode, ls.status as sessionStatus ")
                    .append("from lottery_ticket lt ")
                    .append("join app_user au on au.id = lt.user_id ")
                    .append("join lottery_session ls on ls.id = lt.session_id ")
                    .append("join lottery l on l.id = ls.lottery_id ");
            this.schema = sqlQuery.toString();
        }
        public String schema() {
            return this.schema;
        }
        @Override
        public LotteryTicketSearchWsDTO mapRow(ResultSet rs, int rowNum) throws SQLException {
            var id = JdbcSupport.getLong(rs, "id");
            var userId = JdbcSupport.getLong(rs, "userId");
            var username = rs.getString("username");
            var fullName = rs.getString("fullName");
            var mobileNo = rs.getString("mobileNo");
            var code = rs.getString("code");
            var numbers = rs.getString("numbers");
            var quantity = rs.getInt("quantity");
            var price = rs.getLong("price");
            var prizeMoney = rs.getLong("prizeMoney");
            var win = rs.getBoolean("win");
            var status = rs.getInt("status");
            var lotteryName = rs.getString("lotteryName");
            var lotteryCode = rs.getString("lotteryCode");
            var sessionStatus = rs.getString("sessionStatus");

            final var createdDate = JdbcSupport.getLocalDateTime(rs, "createdDate");
            String createdDateStr = "";
            if (createdDate != null) {
                var formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");
                createdDateStr = formatter.format(createdDate);
            }

            return new LotteryTicketSearchWsDTO(id, userId, username, fullName, mobileNo, lotteryName, lotteryCode,
                    sessionStatus, code, numbers, quantity, price, prizeMoney,win, status, createdDateStr);
        }
    }
}
