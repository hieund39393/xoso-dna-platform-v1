package com.xoso.user.wsdto;

import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class ChangePasswordWsDTO {
    @NotNull
    private String oldPassword;

    @NotNull
    private String newPassword;

}
