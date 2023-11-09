package com.xoso.agency.wsdto;

import lombok.Data;

import javax.validation.constraints.NotNull;
@Data
public class AgencyCreateWsDTO {
    @NotNull
    private String email;
    @NotNull
    private String fullName;
    @NotNull
    private String accountName;
    @NotNull
    private String accountNumber;
    @NotNull
    private Long bankId;
    @NotNull
    private String captchaValue;
    @NotNull
    private String captchaId;
}
