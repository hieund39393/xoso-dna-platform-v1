package com.xoso.infrastructure.security.repository;


import com.xoso.infrastructure.security.domain.PlatformUser;

public interface PlatformUserRepository {
    PlatformUser findByUsernameAndDeletedAndEnabled(String username, boolean deleted, boolean enabled);
}
