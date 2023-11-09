package com.xoso.lottery.wsdto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class LotteryModeWsDTO {
    private Long id;
    private String code;
    private String name;
    private long price;
    private long prizeMoney;
}
