package com.xoso.user.wsdto;

import lombok.Data;

@Data
public class PasswordWithdrawCreateRequestWsDTO {
    private String username;
    private String password;
    private String passwordWithdraw;
}
