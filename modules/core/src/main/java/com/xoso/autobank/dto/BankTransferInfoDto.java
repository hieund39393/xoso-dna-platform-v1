package com.xoso.autobank.dto;

import lombok.Builder;
import lombok.Data;
import lombok.ToString;

import java.math.BigDecimal;

@Data
@Builder
@ToString
public class BankTransferInfoDto {
    public String content;
    public BigDecimal amount;
    public String xref;
}
