package com.xoso.wallet.service.impl;

import com.xoso.infrastructure.core.data.SearchParameters;
import com.xoso.infrastructure.core.service.JdbcSupport;
import com.xoso.infrastructure.core.service.Page;
import com.xoso.infrastructure.core.service.PaginationHelper;
import com.xoso.wallet.data.WalletTransactionData;
import com.xoso.wallet.service.WalletTransactionReadService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

@Service
public class WalletTransactionReadServiceImpl implements WalletTransactionReadService {


    private final TransactionMapper transactionMapper = new TransactionMapper();
    private final PaginationHelper paginationHelper;
    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public WalletTransactionReadServiceImpl(PaginationHelper paginationHelper, JdbcTemplate jdbcTemplate) {
        this.paginationHelper = paginationHelper;
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public Page<WalletTransactionData> retrieveAll(SearchParameters searchParameters) {
        final StringBuilder sqlBuilder = new StringBuilder(200);
        var paramList = new ArrayList<Object>();

        sqlBuilder.append("select ");
        sqlBuilder.append(this.transactionMapper.schema);

        String extraCriteria = " where 1=1 ";
        if(searchParameters != null) {
            var searchValue = searchParameters.getSearchValue();
            var status = searchParameters.getStatus();
            var walletId = searchParameters.getWalletId();
            var userId = searchParameters.getUserId();

            if (StringUtils.isNotBlank(searchValue)) {
                extraCriteria += " and " +
                        "(u.username like ? " +
                        "or u.full_name like ? " +
                        "or u.full_name like ? " +
                        "or txt.content like ? " +
                        "or u.mobile_no like ?) ";
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
                extraCriteria += " and txt.status in " + sqlStatus;
            }

            var fromDate = searchParameters.getFromDate();
            var toDate = searchParameters.getToDate();

            String fromDateStr = null;
            String toDateStr = null;

            final DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
            if (fromDate != null) {
                fromDateStr = df.format(fromDate);
                extraCriteria += " and txt.submitted_date >=  '" + fromDateStr + "'";
            }
            if (toDate != null) {
                toDateStr = df.format(toDate);
                extraCriteria += " and txt.submitted_date < '" + toDateStr + "'";
            }

            if (walletId != null) {
                extraCriteria += " and w.id = ? ";
                paramList.add(walletId);
            }

            if (userId != null) {
                extraCriteria += " and u.id = ? ";
                paramList.add(userId);
            }

        }

        sqlBuilder.append(extraCriteria);
        if (StringUtils.isNotBlank(searchParameters.getOrderBy())) {
            sqlBuilder.append(" order by ").append(searchParameters.getOrderBy());
        } else {
            sqlBuilder.append(" order by txt.submitted_date DESC ");
        }

        final var sqlBuilderNoPaging = sqlBuilder.toString();
        if (searchParameters.isLimited()) {
            sqlBuilder.append(" limit ").append(searchParameters.getLimit());
            if (searchParameters.isOffset()) {
                sqlBuilder.append(" offset ").append(searchParameters.getLimit()*searchParameters.getOffset());
            }
        } else {
            sqlBuilder.append(" limit 1000 offset 0 ");
        }

        return this.paginationHelper.fetchPage(this.jdbcTemplate, sqlBuilder.toString(), sqlBuilderNoPaging,
                paramList.toArray(), this.transactionMapper);
    }

    @Override
    public WalletTransactionData retrieveOne(Long transactionId) {
        final String sql = "select " + this.transactionMapper.schema()
                + " where txt.id = ?";
        return this.jdbcTemplate.queryForObject(sql, this.transactionMapper, new Object[] { transactionId });
    }

    private static final class TransactionMapper implements RowMapper<WalletTransactionData> {

        private final String schema;

        private TransactionMapper() {
            final var builder = new StringBuilder(400);
            builder.append(" txt.id as id, txt.transaction_no as transactionNo, txt.amount as amount, txt.fee as fee, txt.status as transactionStatus, txt.submitted_date as submittedDate, txt.approved_date as approvedDate, ");
            builder.append(" txt.approved_by as approvedBy, txt.rejected_by as rejectedBy, txt.created_by as submittedBy, ");
            builder.append("txt.transaction_type as transactionType, txt.rejected_date as rejectedDate, txt.content as content, ");
            builder.append(" u.id as userId, u.username as username, u.full_name as fullName, u.mobile_no as phoneNumber, ");
            builder.append(" cba.account_number as accountNumber, b.code as bankCode, b.name as bankName, ");
            builder.append(" wMaster.id as walletMasterId, ");
            builder.append(" w.id as walletId ");
            builder.append("from wallet_transactions txt ");
            builder.append("join app_user u on u.id = txt.user_id ");
            builder.append("join wallets wMaster on wMaster.id = txt.wallet_master_id ");
            builder.append("join wallets w on w.id = txt.wallet_id ");
            builder.append("left join client_bank_account cba on cba.id = txt.client_bank_account_id ");
            builder.append("left join bank b on b.id = cba.bank_id ");
            this.schema = builder.toString();
        }

        public String schema() {
            return this.schema;
        }

        @Override
        public WalletTransactionData mapRow(ResultSet rs, int rowNum) throws SQLException {
            final var id = JdbcSupport.getLong(rs, "id");
            final var transactionNo = rs.getString( "transactionNo");
            final var amount = rs.getBigDecimal("amount");
            final var fee = rs.getBigDecimal("fee");
            final var transactionStatus = rs.getString("transactionStatus");
            final var transactionType = rs.getString("transactionType");
            //TODO, hot fix language
            String type =  "";
            if(transactionType.equalsIgnoreCase("DEPOSIT"))
                type = "ເງິນຝາກ";
            else
                type = "ຖອນ";
            String status = "";
            if(transactionStatus.equalsIgnoreCase("NEW"))
                status = "ໃຫມ່";
            else if(transactionStatus.equalsIgnoreCase("PROCESSING"))
                status = "ການປຸງແຕ່ງ";
            else if(transactionStatus.equalsIgnoreCase("APPROVED"))
                status = "ອະນຸມັດ";
            else
                status = "ປະຕິເສດ";
            final var submittedDate = JdbcSupport.getLocalDateTime(rs, "submittedDate");
            String submittedDateStr = "";
            if (submittedDate != null) {
                var formatter = DateTimeFormatter.ofPattern("HH:mm:ss dd-MM-yyyy");
                submittedDateStr = formatter.format(submittedDate);
            }

            final var approvedDate = JdbcSupport.getLocalDateTime(rs, "approvedDate");
            String approvedDateStr = "";
            if (approvedDate != null) {
                var formatter = DateTimeFormatter.ofPattern("HH:mm:ss dd-MM-yyyy");
                approvedDateStr = formatter.format(approvedDate);
            }

            final var rejectedDate = JdbcSupport.getLocalDateTime(rs, "submittedDate");
            String rejectedDateStr = "";
            if (rejectedDate != null) {
                var formatter = DateTimeFormatter.ofPattern("HH:mm:ss dd-MM-yyyy");
                rejectedDateStr = formatter.format(rejectedDate);
            }

            final var userId = JdbcSupport.getLong(rs, "userId");
            final var username = rs.getString("username");
            final var fullName = rs.getString("fullName");
            final var phoneNumber = rs.getString("phoneNumber");

            final var bankCode = rs.getString("bankCode");
            final var bankName = rs.getString("bankName");
            final var accountNumber = rs.getString("accountNumber");
            final var content = rs.getString("content");

            final var walletId = JdbcSupport.getLong(rs, "walletId");
            final var walletMasterId = JdbcSupport.getLong(rs, "walletMasterId");

            final var approvedBy = rs.getString("approvedBy");
            final var rejectedBy = rs.getString("rejectedBy");
            final var submittedBy = rs.getString("submittedBy");

            return new WalletTransactionData(id, amount, fee, type, status, transactionNo, bankCode, bankName, accountNumber,
                    content, userId, username, fullName, phoneNumber, submittedDateStr, approvedDateStr, rejectedDateStr, submittedBy, approvedBy, rejectedBy, walletMasterId, walletId);
        }
    }
}
