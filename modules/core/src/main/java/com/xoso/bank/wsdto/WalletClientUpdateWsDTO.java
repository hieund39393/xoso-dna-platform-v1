package com.xoso.bank.wsdto;

import com.xoso.bank.data.ClientBankAccountData;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class WalletClientUpdateWsDTO {
    private Long id;
    private BigDecimal balance;
    private List<ClientBankAccountData> bankAccounts;
}
