package com.xoso.api.auth.wsdto;

import lombok.Data;

import javax.validation.constraints.Pattern;

@Data
public class LoginWsDTO {
    private String username;
    private String password;

}
