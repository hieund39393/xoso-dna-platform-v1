package com.xoso.service;

import com.xoso.user.wsdto.GeneratePasswordWsDTO;

public interface AdminUserService {
    GeneratePasswordWsDTO generatePassword(Long userId);
}
