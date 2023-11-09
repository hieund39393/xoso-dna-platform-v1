package com.xoso.lottery.wsdto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class LotteryCategoryRequestWsDTO {

    private Long id;

    @NotNull(message = "name is required parameter")
    private String name;

    @NotNull(message = "code is required parameter")
    private String code;
    private boolean active;
    private boolean campaign;
}
