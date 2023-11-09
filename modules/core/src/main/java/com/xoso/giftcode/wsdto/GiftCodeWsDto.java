package com.xoso.giftcode.wsdto;

import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class GiftCodeWsDto {
    @NotNull
    String code;
}
