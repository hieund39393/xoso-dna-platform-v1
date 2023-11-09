package com.xoso.bank.wsdto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ClientBankAccountCreateWsDTO {
    private String accountName;
    @NotNull
    private String accountNumber;
    private String cardNumber;
    @NotNull
    private Long bankId;
    private boolean enabled;

    private Long walletId;

    public ClientBankAccountCreateWsDTO(String accountName, String accountNumber, Long bankId) {
        this.accountName = accountName;
        this.accountNumber = accountNumber;
        this.bankId = bankId;
    }

    public ClientBankAccountCreateWsDTO(Long walletId) {
        this.walletId = walletId;
    }
}
