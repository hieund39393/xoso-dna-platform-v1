package com.xoso.lottery.wsdto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class LotteryVideoWsDTO {
    private Long id;
    private int group;
    private int index;
    private int number;
    private long duration;
    private String url;
}
