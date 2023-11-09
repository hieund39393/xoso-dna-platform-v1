package com.xoso.bank.service.impl;

import com.xoso.bank.service.ClientBankAccountReadService;
import com.xoso.bank.wsdto.WalletClientUpdateWsDTO;
import com.xoso.infrastructure.security.service.AuthenticationService;
import com.xoso.user.repository.AppUserRepository;
import com.xoso.wallet.exception.WalletNotFountException;
import com.xoso.wallet.repository.WalletRepository;
import org.apache.commons.lang3.StringUtils;
import com.xoso.bank.data.ClientBankAccountData;
import com.xoso.infrastructure.core.data.SearchParameters;
import com.xoso.infrastructure.core.service.JdbcSupport;
import com.xoso.infrastructure.core.service.Page;
import com.xoso.infrastructure.core.service.PaginationHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@Service
public class ClientBankAccountReadServiceImpl implements ClientBankAccountReadService {

    private final ClientBankAccountMapper bankAccountMapper = new ClientBankAccountMapper();

    private final PaginationHelper paginationHelper;
    private final JdbcTemplate jdbcTemplate;
    private final AuthenticationService authenticationService;
    private final AppUserRepository appUserRepository;
    private final WalletRepository walletRepository;

    @Autowired
    public ClientBankAccountReadServiceImpl(PaginationHelper paginationHelper, JdbcTemplate jdbcTemplate,
                                            AuthenticationService authenticationService, AppUserRepository appUserRepository, WalletRepository walletRepository) {
        this.paginationHelper = paginationHelper;
        this.jdbcTemplate = jdbcTemplate;
        this.authenticationService = authenticationService;
        this.appUserRepository = appUserRepository;
        this.walletRepository = walletRepository;
    }

    @Override
    public Page<ClientBankAccountData> retrieveAll(SearchParameters searchParameters) {

        final StringBuilder sqlBuilder = new StringBuilder(200);
        var paramList = new ArrayList<Object>();

        sqlBuilder.append("select ");
        sqlBuilder.append(this.bankAccountMapper.schema);

        var extraCriteria = " where 1=1 ";
        if (searchParameters != null) {
            var searchValue = searchParameters.getSearchValue();
            var status = searchParameters.getStatus();

            if (StringUtils.isNotBlank(searchValue)) {
                extraCriteria += " and " +
                        "(app.username like ? " +
                        "or app.full_name like ? " +
                        "or app.mobile_no like ?) ";
                var userInfo = "%" + searchValue + "%";
                paramList.add(userInfo);
                paramList.add(userInfo);
                paramList.add(userInfo);
            }

            if (!CollectionUtils.isEmpty(status)) {
                var sqlStatus = "(";
                var index = 0;
                for (String statusValue : status) {
                    if (index == status.size() - 1) {
                        sqlStatus += String.format("%s%s%s", "'", statusValue, "'");
                    } else {
                        sqlStatus += String.format("%s%s%s%s", "'", statusValue, "'", ",");
                    }
                    index++;
                }
                sqlStatus += ")";
                extraCriteria += " and cba.status in " + sqlStatus;
            }

            if (searchParameters.getUserId() != null) {
                extraCriteria += " and app.id = ? ";
                paramList.add(searchParameters.getUserId());
            }

            if (searchParameters.getDeleted() != null) {
                extraCriteria += " and cba.deleted = ? ";
                paramList.add(searchParameters.getDeleted());
            }
        }

        sqlBuilder.append(extraCriteria);
        if (StringUtils.isNotBlank(searchParameters.getOrderBy())) {
            sqlBuilder.append(" order by ").append(searchParameters.getOrderBy());
        } else {
            sqlBuilder.append(" order by cba.created_date DESC ");
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
                paramList.toArray(), this.bankAccountMapper);
    }

    @Override
    public ClientBankAccountData retrieveOne(Long bankAccountId) {
        final String sql = "select " + this.bankAccountMapper.schema()
                + " where cba.id = ?";
        return this.jdbcTemplate.queryForObject(sql, this.bankAccountMapper, new Object[] { bankAccountId });
    }

    @Override
    public List<ClientBankAccountData> retrieveByUser() {
        var currentUser = this.authenticationService.authenticatedUser();
        var appUser = this.appUserRepository.findAppUserByName(currentUser.getUsername());
        var searchParameters = new SearchParameters();
        searchParameters.setUserId(appUser.getId());
        searchParameters.setDeleted(Boolean.FALSE);
        var result = this.retrieveAll(searchParameters);
        return result.getPageItems();
    }

    @Override
    public WalletClientUpdateWsDTO retrieveByWallet(Long walletId) {
        var wallet = this.walletRepository.queryFindById(walletId);
        if (wallet == null) {
            throw new WalletNotFountException();
        }
        var appUser = wallet.getUser();

        var searchParameters = new SearchParameters();
        searchParameters.setUserId(appUser.getId());
        searchParameters.setDeleted(Boolean.FALSE);
        var result = this.retrieveAll(searchParameters);
        var data = new WalletClientUpdateWsDTO();
        data.setBalance(wallet.getBalance());
        data.setBankAccounts(result.getPageItems());
        return data;
    }

    @Override
    public List<ClientBankAccountData> retrieveAllByWallet(Long walletId) {
        var wallet = this.walletRepository.queryFindById(walletId);
        if (wallet == null) {
            throw new WalletNotFountException();
        }
        var appUser = wallet.getUser();

        var searchParameters = new SearchParameters();
        searchParameters.setUserId(appUser.getId());
        searchParameters.setDeleted(Boolean.FALSE);
        var result = this.retrieveAll(searchParameters);
        return result.getPageItems();
    }

    private static final class ClientBankAccountMapper implements RowMapper<ClientBankAccountData> {

        private final String schema;

        private ClientBankAccountMapper() {
            final var builder = new StringBuilder(400)
                    .append("cba.id as id, ")
                    .append("app.id as userId, app.username as username, app.full_name as fullName, app.mobile_no as mobileNo, ")
                    .append("b.id as bankId, b.name as bankName, b.code as bankCode, ")
                    .append("cba.account_name as accountName, cba.account_number as accountNumber, ")
                    .append("cba.card_number as cardNumber, cba.enabled as enabled ")
                    .append("from client_bank_account cba ")
                    .append("join app_user app on app.id = cba.user_id ")
                    .append("join bank b on b.id = cba.bank_id ");
            this.schema = builder.toString();
        }

        public String schema() {
            return this.schema;
        }

        @Override
        public ClientBankAccountData mapRow(ResultSet rs, int rowNum) throws SQLException {
            final var id = JdbcSupport.getLong(rs, "id");
            final var userId = JdbcSupport.getLong(rs, "userId");
            final var username = rs.getString("username");
            final var fullName = rs.getString("fullName");
            final var mobileNo = rs.getString("mobileNo");

            final var bankId = JdbcSupport.getLong(rs, "bankId");
            final var bankName = rs.getString("bankName");
            final var bankCode = rs.getString("bankCode");

            final var accountName = rs.getString("accountName");
            final var accountNumber = rs.getString("accountNumber");

            final var cardNumber = rs.getString("cardNumber");
            final var enabled = rs.getBoolean("enabled");

            return new ClientBankAccountData(id, userId, username, fullName, mobileNo, bankId, bankCode, bankName, accountName, accountNumber, cardNumber, enabled);
        }


    }
}
