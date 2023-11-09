package com.xoso.lottery.wsdto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class LotteryRequestWsDTO {

    private Long id;
    @NotEmpty(message = "name is required parameter")
    private String name;
    @NotEmpty(message = "code is required parameter")
    private String code;
    @NotEmpty(message = "type is required parameter")
    private String type;
    @NotEmpty(message = "modes is required parameter")
    private String modes;
    @NotNull(message = "hour is required parameter")
    private int hour;
    @NotNull(message = "min is required parameter")
    private int min;
    @NotNull(message = "sec is required parameter")
    private int sec;
    private boolean active;
    private boolean vip;
    @NotNull(message = "lotteryCategory is required parameter")
    private Long lotteryCategoryId;
}
