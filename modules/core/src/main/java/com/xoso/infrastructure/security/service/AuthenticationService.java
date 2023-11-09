package com.xoso.infrastructure.security.service;

import com.xoso.infrastructure.security.domain.PlatformUser;
import com.xoso.user.model.AppUser;

public interface AuthenticationService {
    AppUser authenticatedUser();
}
