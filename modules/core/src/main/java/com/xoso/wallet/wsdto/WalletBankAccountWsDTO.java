package com.xoso.wallet.wsdto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class WalletBankAccountWsDTO {
    private Long id;
    private String accountName;
    private String accountNumber;
    private String cardNumber;
    private boolean enabled;
    private Long bankId;
    private String password;
    private String userName;
}
