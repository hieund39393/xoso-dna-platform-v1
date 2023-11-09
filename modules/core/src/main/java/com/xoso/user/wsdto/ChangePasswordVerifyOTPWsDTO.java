package com.xoso.user.wsdto;

import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class ChangePasswordVerifyOTPWsDTO {
    @NotNull
    private String username;
    @NotNull
    private String password;
    @NotNull
    private String otp;
}
