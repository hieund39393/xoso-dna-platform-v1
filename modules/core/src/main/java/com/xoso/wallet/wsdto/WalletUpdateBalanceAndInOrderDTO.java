package com.xoso.wallet.wsdto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

import java.math.BigDecimal;

@ToString
@Getter
@Builder
@AllArgsConstructor
public class WalletUpdateBalanceAndInOrderDTO {

    private long walletId;
    private BigDecimal balance;
    private BigDecimal inOrder;
}

