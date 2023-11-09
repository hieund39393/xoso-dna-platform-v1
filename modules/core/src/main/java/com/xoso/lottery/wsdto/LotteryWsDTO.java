package com.xoso.lottery.wsdto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class LotteryWsDTO {
    private Long id;
    private String name;
    private String code;
    private String type;
    private String modes;
    private int hour;
    private int min;
    private int sec;
    private boolean active;
    private boolean vip;
    private String categoryCode;
    private String categoryName;
    private Long categoryId;
}
