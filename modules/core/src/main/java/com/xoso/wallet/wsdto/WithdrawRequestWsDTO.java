package com.xoso.wallet.wsdto;

import lombok.*;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import java.math.BigDecimal;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class WithdrawRequestWsDTO {
    @Min(20000)
    @Max(200000000)
    private BigDecimal amount;
    private Long bankId;
    private String accountNumber;
    private String passwordWithdraw;
}
