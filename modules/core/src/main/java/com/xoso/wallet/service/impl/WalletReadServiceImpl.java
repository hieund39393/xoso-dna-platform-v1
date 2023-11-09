package com.xoso.wallet.service.impl;

import com.xoso.bank.data.ClientBankAccountData;
import com.xoso.bank.data.MasterBankAccountData;
import com.xoso.bank.service.ClientBankAccountReadService;
import com.xoso.infrastructure.core.data.SearchParameters;
import com.xoso.infrastructure.core.service.JdbcSupport;
import com.xoso.infrastructure.core.service.Page;
import com.xoso.infrastructure.core.service.PaginationHelper;
import com.xoso.wallet.data.WalletData;
import com.xoso.wallet.service.WalletReadService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Service
public class WalletReadServiceImpl implements WalletReadService {
    private final PaginationHelper paginationHelper;
    private final JdbcTemplate jdbcTemplate;
    private final ClientBankAccountReadService clientBankAccountReadService;

    @Autowired
    public WalletReadServiceImpl(PaginationHelper paginationHelper, JdbcTemplate jdbcTemplate, ClientBankAccountReadService clientBankAccountReadService) {
        this.paginationHelper = paginationHelper;
        this.jdbcTemplate = jdbcTemplate;
        this.clientBankAccountReadService = clientBankAccountReadService;
    }

    @Override
    public Page<WalletData> retrieveAll(SearchParameters searchParameters, boolean isMaster) {

        final var walletMapper = new WalletMapper(clientBankAccountReadService, false);
        final StringBuilder sqlBuilder = new StringBuilder(200);
        var paramList = new ArrayList<Object>();

        sqlBuilder.append("select ");
        sqlBuilder.append(walletMapper.schema);

        String extraCriteria = " where 1=1 ";
        if( searchParameters != null) {
            var searchValue = searchParameters.getSearchValue();
            var status = searchParameters.getStatus();
            var oneServices = searchParameters.getOneServices();

            if (StringUtils.isNotBlank(searchValue)) {
                extraCriteria += " and " +
                        "(CAST(app.id AS varchar) like ? " +
                        "or app.username like ? " +
                        "or cb.account_number like ? " +
                        "or app.mobile_no like ? " +
                        "or c.national_id like ?) ";
                String clientInfo = "%" + searchValue + "%";
                paramList.add(clientInfo);
                paramList.add(clientInfo);
                paramList.add(clientInfo);
                paramList.add(clientInfo);
                paramList.add(clientInfo);
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
                extraCriteria += " and w.status in " + sqlStatus;
            }

            if (!CollectionUtils.isEmpty(oneServices)) {
                var sqlOneService = "(";
                var index = 0;
                for (Long serviceId : oneServices) {
                    if (index == oneServices.size() - 1) {
                        sqlOneService += serviceId;
                    } else {
                        sqlOneService += String.format("%s%", serviceId, ",");
                    }
                    index++;
                }
                sqlOneService += ")";
                extraCriteria += " and ser.id in " + sqlOneService;
            }
        }

        if (isMaster) {
            extraCriteria += " and w.is_master = true ";
        } else {
            extraCriteria += " and w.is_master = false ";
        }

        sqlBuilder.append(extraCriteria);
        if (StringUtils.isNotBlank(searchParameters.getOrderBy())) {
            sqlBuilder.append(" order by ").append(searchParameters.getOrderBy());
        } else
            sqlBuilder.append(" order by w.id DESC ");

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
                paramList.toArray(), walletMapper);
    }

    @Override
    public WalletData retrieveOne(Long walletId, boolean addBankAccounts) {
        final var walletMapper = new WalletMapper(clientBankAccountReadService, addBankAccounts);
        final String sql = "select " + walletMapper.schema()
                + " where w.id = ?";
        return this.jdbcTemplate.queryForObject(sql, walletMapper, new Object[] { walletId });
    }

    private static final class MasterBankAccountMapper implements RowMapper<MasterBankAccountData> {
        private final StringBuilder sqlQuery = new StringBuilder()
                .append("bankAccount.id as id,bankAccount.user_name as userName, bankAccount.account_name as accountName, bankAccount.account_number as accountNumber, ")
                .append("bankAccount.card_number as cardNumber, bankAccount.enabled as enabled, bankAccount.password as password, ")
                .append("b.id as bankId, b.code as bankCode, b.name as bankName ")
                .append("from master_bank_account as bankAccount ")
                .append("join bank b on b.id = bankAccount.bank_id ");
        @Override
        public MasterBankAccountData mapRow(ResultSet rs, int rowNum) throws SQLException {
            final var id = JdbcSupport.getLong(rs, "id");
            final var bankId = JdbcSupport.getLong(rs, "bankId");
            final var accountName = rs.getString("accountName");
            final var accountNumber = rs.getString("accountNumber");
            final var cardNumber = rs.getString("cardNumber");
            final var enabled = rs.getBoolean("enabled");
            final var bankCode = rs.getString("bankCode");
            final var bankName = rs.getString("bankName");
            final var password = rs.getString("password");
            final var userName = rs.getString("userName");
            return new MasterBankAccountData(id, bankId, bankCode, bankName, accountName, accountNumber, cardNumber, enabled, password, userName);
        }

        public String schema() {
            return sqlQuery.toString();
        }
    }

    private final class WalletMapper implements RowMapper<WalletData> {

        private final String schema;
        private final boolean addBankAccounts;

        public WalletMapper(ClientBankAccountReadService clientBankAccountReadService, boolean addBankAccounts) {
            this.addBankAccounts = addBankAccounts;

            final var builder = new StringBuilder(400);
            builder.append("w.id as id, app.id as userId, ");
            builder.append("c.id as clientId, c.account_no as accountNo, app.username as username, app.full_name as fullName, app.mobile_no as mobileNo, c.national_id as nationalId, ");
            builder.append("w.is_master as isMaster, bank.code as bank_code, cb.account_number as account_number,");
            builder.append("w.balance as balance, w.total_win as total_win,w.total_bet as total_bet,w.total_deposit as total_deposit,w.total_withdraw as total_withdraw,w.status as status, w.created_date as createdDate ");
            builder.append("from wallets w ");
            builder.append("join app_user app on app.id = w.user_id ");
            builder.append("left join client c on c.id = app.client_id ");
            builder.append("left join client_bank_account cb on w.user_id = cb.user_id ");
            builder.append("left join bank bank on cb.bank_id = bank.id");
            this.schema = builder.toString();

        }

        public String schema() {
            return this.schema;
        }

        @Override
        public WalletData mapRow(ResultSet rs, int rowNum) throws SQLException {
            final var id = JdbcSupport.getLong(rs, "id");
            final var clientId = JdbcSupport.getLong(rs, "clientId");
            final var userId = JdbcSupport.getLong(rs, "userId");
            final var accountNo = rs.getString("accountNo");
            final var username = rs.getString("username");
            final var fullName = rs.getString("fullName");
            final var mobileNo = rs.getString("mobileNo");
            final var nationalId = rs.getString("nationalId");

            final boolean isMaster = rs.getBoolean("isMaster");
            final var balance = rs.getBigDecimal("balance");
            final var totalWin = rs.getBigDecimal("total_win");
            final var totalBet = rs.getBigDecimal("total_bet");
            final var totalDeposit = rs.getBigDecimal("total_deposit");
            final var totalWithdraw = rs.getBigDecimal("total_withdraw");
            final var status = rs.getString("status");
            final var createdDate = JdbcSupport.getLocalDate(rs, "createdDate");

            final var bankCode = rs.getString("bank_code");
            final var accountNumber= rs.getString("account_number");

            String createdDateStr = "";
            if (createdDate != null) {
                var formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
                createdDateStr = formatter.format(createdDate);
            }
            List<MasterBankAccountData> bankAccounts = null;
            List<ClientBankAccountData> clientBankAccounts = null;
            if (addBankAccounts) {
                if (isMaster) {
                    var masterBankAccountMapper = new MasterBankAccountMapper();
                    var sql = "select " + masterBankAccountMapper.schema() + " where bankAccount.wallet_id = " + id
                            + " order by bankAccount.created_date desc ";
                    bankAccounts = jdbcTemplate.query(sql, masterBankAccountMapper);
                } else {
                    clientBankAccounts = clientBankAccountReadService.retrieveAllByWallet(id);
                }

            }

            return new WalletData(id, userId, clientId, accountNo, mobileNo, fullName, username, nationalId, balance, isMaster, status, createdDateStr,bankCode, accountNumber, totalWin,totalBet,totalWithdraw,totalDeposit, bankAccounts, clientBankAccounts);
        }
    }
}
