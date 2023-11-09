package com.xoso.api.security.service.impl;

import com.xoso.api.security.service.PlatformUserDetailsService;
import com.xoso.infrastructure.security.repository.PlatformUserRepository;
import com.xoso.user.exception.CustomLockedException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service(value = "userDetailsService")
public class PlatformUserDetailsServiceImpl implements PlatformUserDetailsService {

    @Autowired
    private PlatformUserRepository platformUserRepository;
    @Override
    // @Cacheable(value = "usersByUsername", key = "username-invest")
    public UserDetails loadUserByUsername(final String username) throws UsernameNotFoundException, DataAccessException {

        // Retrieve active users only
        final boolean deleted = false;
        final boolean enabled = true;

        final var appUser = this.platformUserRepository.findByUsernameAndDeletedAndEnabled(username, deleted, enabled);

        if (appUser == null) { throw new UsernameNotFoundException(username + ": not found"); }

        if (!appUser.isAccountNonLocked()) {
            throw new CustomLockedException();
        }
        return appUser;
    }
}
