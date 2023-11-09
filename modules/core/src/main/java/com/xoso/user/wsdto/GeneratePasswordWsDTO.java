package com.xoso.user.wsdto;

import lombok.Data;

@Data
public class GeneratePasswordWsDTO {
    private String username;
    private String mobileNo;
    private String password;
}
