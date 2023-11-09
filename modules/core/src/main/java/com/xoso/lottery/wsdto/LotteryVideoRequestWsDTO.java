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
public class LotteryVideoRequestWsDTO {
    private Long id;
    private int group;
    private int index;
    private int number;
    @NotNull(message = "duration is required parameter")
    private long duration;

    @NotEmpty(message = "url is required parameter")
    private String url;
}
