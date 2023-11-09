package com.xoso.wallet.wsdto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import java.math.BigDecimal;
import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ApproveTransactionRequestWsDTO {
    private BigDecimal amount;
    private String refNumber;
    private String comment;

    @DateTimeFormat(pattern = "dd/MM/yyyy")
    private Date refDate;
}
