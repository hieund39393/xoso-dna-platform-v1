package com.xoso.wallet.data;

import com.xoso.wallet.model.TransactionType;
import com.xoso.wallet.model.WalletTransactions;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class WalletTransactionData {
    private Long id;
    private BigDecimal amount;
    private BigDecimal fee;
    private String transactionType;
    private String transactionStatus;
    private String transactionNo;

    private String bankCode;
    private String bankName;
    private String accountNumber;
    private String content;

    private Long userId;
    private String username;
    private String fullName;
    private String mobileNo;

    private String submittedDate;
    private String approvedDate;
    private String rejectedDate;

    private String submittedBy;
    private String approvedBy;
    private String rejectedBy;

    private Long walletId;
    private Long walletMasterId;

    public static WalletTransactionData converter(WalletTransactions entity) {
        String bankCode = null;
        String bankName = null;
        String accountNumber = null;
        if (TransactionType.DEPOSIT.equals(entity.getTransactionType())) {
            var masterBankAccount = entity.getMasterBankAccount();
            var bank = masterBankAccount.getBank();
            bankCode = bank.getCode();
            bankName = bank.getName();
            accountNumber = masterBankAccount.getAccountNumber();
        } else if (TransactionType.WITHDRAW.equals(entity.getTransactionType())) {
            var clientBankAccount = entity.getClientBankAccount();
            var bank = clientBankAccount.getBank();
            bankCode = bank.getCode();
            bankName = bank.getName();
            accountNumber = clientBankAccount.getAccountNumber();
        }
        return new WalletTransactionData(entity.getId(), entity.getAmount(), entity.getFee(),
                entity.getTransactionType().getCode(), entity.getStatus().getCode(),
                entity.getTransactionNo(),
                bankCode,
                bankName,
                accountNumber,
                entity.getContent(),
                null,
                null,
                entity.getAppUser().getFullName(),
                entity.getAppUser().getMobileNo(),
                null, null, null, null, null, null, null, null
        );
    }
}
