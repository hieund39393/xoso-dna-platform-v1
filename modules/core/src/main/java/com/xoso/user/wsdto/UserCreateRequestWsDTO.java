package com.xoso.user.wsdto;

import lombok.Data;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import java.util.List;

@Data
public class UserCreateRequestWsDTO {

    @Pattern(regexp = "^(?![0-9])[ -~\u0080-\uFFFF]{6,20}$", message = "The username must be from 6-20 numbers and letters, starting with letters")
    @NotNull
    private String username;

    @Pattern(regexp = "^(?![0-9])[ -~\u0080-\uFFFF]{6,20}$", message = "The password must be from 6-20 numbers and letters")
    @NotNull
    private String password;

    @NotNull
    private String mobileNo;
    @NotNull
    private String captchaValue;
    @NotNull
    private String captchaId;
    private String agency;
}
