package com.xoso.service.impl;

import com.xoso.infrastructure.security.repository.PlatformUserRepository;
import com.xoso.service.PlatformUserDetailsService;
import com.xoso.user.model.AppUser;
import com.xoso.user.model.Role;
import com.xoso.user.repository.AppUserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collection;

@Service(value = "userDetailsService")
public class PlatformUserDetailsServiceImpl implements PlatformUserDetailsService {

    @Autowired
    private PlatformUserRepository platformUserRepository;
    @Autowired
    private AppUserRepository appUserRepository;

    @Override
    // @Cacheable(value = "usersByUsername", key = "username-invest")
    public UserDetails loadUserByUsername(final String username) throws UsernameNotFoundException, DataAccessException {

        // Retrieve active users only
        final boolean deleted = false;
        final boolean enabled = true;

        final var appUser = this.platformUserRepository.findByUsernameAndDeletedAndEnabled(username, deleted, enabled);
        if (appUser == null) { throw new UsernameNotFoundException(username + ": not found"); }
        return new User(appUser.getUsername(), appUser.getPassword(),
                getAuthorities(username));
    }

    private Collection<? extends GrantedAuthority> getAuthorities(String userName) {
        var appUser = appUserRepository.findAppUserByName(userName);
        String[] userRoles = appUser.getRoles().stream().map(Role::getName).toArray(String[]::new);
        return AuthorityUtils.createAuthorityList(userRoles);
    }
}
