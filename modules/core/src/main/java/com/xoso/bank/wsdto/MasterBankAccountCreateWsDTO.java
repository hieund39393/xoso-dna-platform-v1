package com.xoso.bank.wsdto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MasterBankAccountCreateWsDTO {
    private String accountName;
    private String accountNumber;
    private String cardNumber;
    private boolean enabled;
    private Long bankId;
    private Long walletId;
    private String passwordBankAccount;
    private String userName;

    public MasterBankAccountCreateWsDTO(Long walletId) {
        this.walletId = walletId;
    }
}
