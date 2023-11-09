package com.xoso.wallet.wsdto;

import lombok.Data;

import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

@Data
public class AdminDepositRequestWsDTO {
    @NotNull
    private BigDecimal amount;
    @NotNull
    private String content;
}
