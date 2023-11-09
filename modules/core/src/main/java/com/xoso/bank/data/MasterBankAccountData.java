package com.xoso.bank.data;

import com.xoso.bank.model.ClientBankAccount;
import com.xoso.bank.model.MasterBankAccount;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class MasterBankAccountData implements Serializable {
    private Long id;
    private Long bankId;
    private String bankCode;
    private String bankName;
    private String accountName;
    private String accountNumber;
    private String cardNumber;
    private boolean enabled;
    private String password;
    private String userName;
    public static MasterBankAccountData fromEntity(MasterBankAccount entity){
        return MasterBankAccountData
                .builder()
                .id(entity.getId())
                .bankId(entity.getBank().getId())
                .bankCode(entity.getBank().getCode())
                .bankName(entity.getBank().getName())
                .userName(entity.getUserName())
                .accountName(entity.getAccountName())
                .cardNumber(entity.getCardNumber())
                .accountNumber(entity.getAccountNumber())
                .enabled(entity.isEnabled())
                .build();
    }
}
