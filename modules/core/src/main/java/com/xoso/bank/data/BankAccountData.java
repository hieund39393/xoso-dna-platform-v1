package com.xoso.bank.data;

import com.xoso.bank.model.ClientBankAccount;
import com.xoso.bank.model.MasterBankAccount;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class BankAccountData {
    private Long id;
    private String bankCode;
    private String bankName;
    private String bankDescription;
    private String accountName;
    private String cardNumber;
    private String accountNumber;
    private boolean enabled;

    public static BankAccountData fromEntity(ClientBankAccount entity){
        return BankAccountData
                .builder()
                .id(entity.getId())
                .bankCode(entity.getBank().getCode())
                .bankName(entity.getBank().getName())
                .bankDescription(entity.getBank().getDescription())
                .accountName(entity.getAccountName())
                .cardNumber(entity.getCardNumber())
                .accountNumber(entity.getAccountNumber())
                .enabled(entity.getBank().isEnabled())
                .build();
    }

    public static BankAccountData fromEntity(MasterBankAccount entity){
        return BankAccountData
                .builder()
                .id(entity.getId())
                .bankCode(entity.getBank().getCode())
                .bankName(entity.getBank().getName())
                .bankDescription(entity.getBank().getDescription())
                .accountName(entity.getAccountName())
                .cardNumber(entity.getCardNumber())
                .accountNumber(entity.getAccountNumber())
                .enabled(entity.getBank().isEnabled())
                .build();
    }
}
