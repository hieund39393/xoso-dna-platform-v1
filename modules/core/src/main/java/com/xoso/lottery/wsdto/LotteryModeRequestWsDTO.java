package com.xoso.lottery.wsdto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotEmpty;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class LotteryModeRequestWsDTO {
    private Long id;
    @NotEmpty(message = "code is required parameter")
    private String code;
    @NotEmpty(message = "name is required parameter")
    private String name;
    private long price;
    private long prizeMoney;
}
