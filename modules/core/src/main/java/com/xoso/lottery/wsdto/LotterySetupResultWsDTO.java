package com.xoso.lottery.wsdto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class LotterySetupResultWsDTO {
    private Long id;
    private String result;
}
