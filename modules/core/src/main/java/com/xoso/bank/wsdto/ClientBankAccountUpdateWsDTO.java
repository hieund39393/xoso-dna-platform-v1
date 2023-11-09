package com.xoso.bank.wsdto;

import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class ClientBankAccountUpdateWsDTO {
    @NotNull
    private Long id;
    @NotNull
    private String accountName;
    @NotNull
    private String accountNumber;
    private String cardNumber;
    @NotNull
    private Long bankId;
}
