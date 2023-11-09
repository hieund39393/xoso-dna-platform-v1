package com.xoso.user.service;

import com.xoso.user.wsdto.PasswordWithdrawCreateRequestWsDTO;

public interface PasswordWithdrawService {
    void create(String username, String passwordWithdraw);
    void updatePasswordByUser(String username, String passwordWithdraw);
}
