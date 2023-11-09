package com.xoso.api.security.service;

import com.xoso.api.security.data.AuthenticatedUserData;

public interface AuthenticateService {
    AuthenticatedUserData login(String username, String password);
    AuthenticatedUserData refreshToken(String token);
}
