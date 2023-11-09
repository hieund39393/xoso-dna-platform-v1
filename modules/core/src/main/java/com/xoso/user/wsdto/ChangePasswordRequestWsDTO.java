package com.xoso.user.wsdto;

import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class ChangePasswordRequestWsDTO {
    @NotNull
    private String username;
    @NotNull
    private String phoneNumber;
}
