package com.xoso.api.security.dao;

import com.xoso.api.exception.InvalidUsernameOrPasswordException;
import com.xoso.user.exception.CustomLockedException;
import com.xoso.user.service.UserWriteService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

public class DaoAuthenticationProvider extends org.springframework.security.authentication.dao.DaoAuthenticationProvider {
    Logger logger = LoggerFactory.getLogger(DaoAuthenticationProvider.class);

    @Autowired
    private UserWriteService userWriteService;

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        var username = (authentication.getPrincipal() == null) ? "NONE_PROVIDED" : authentication.getName();
        return this.createSuccessAuthentication(username, authentication);
    }

    private Authentication createSuccessAuthentication(String username, Authentication authentication) {
        UserDetails user = null;
        try {
            user = retrieveUser(username, (UsernamePasswordAuthenticationToken) authentication);
            additionalAuthenticationChecks(user, (UsernamePasswordAuthenticationToken) authentication);
        } catch (UsernameNotFoundException notFound) {
            logger.debug("User '" + username + "' not found");
            throw new InvalidUsernameOrPasswordException();
        } catch (BadCredentialsException exception) {
            var locked = this.userWriteService.increaseFailedAttempts(username);
            if (locked) {
                throw new CustomLockedException();
            }
            throw new InvalidUsernameOrPasswordException();
        }
        Object principalToReturn = user;
        if (this.isForcePrincipalAsString()) {
            principalToReturn = user.getUsername();
        }
        return createSuccessAuthentication(principalToReturn, authentication, user);
    }

    @Override
    protected Authentication createSuccessAuthentication(Object principal,
                                                         Authentication authentication, UserDetails user) {
        return super.createSuccessAuthentication(principal, authentication, user);
    }
}
