package com.xoso.infrastructure.security.service;

import com.xoso.infrastructure.security.domain.PlatformUser;

public interface PlatformPasswordEncoder {
    String encode(PlatformUser user);
    boolean matches(String rawPassword, String encodedPassword);
}
