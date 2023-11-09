package com.xoso.infrastructure.security.service.impl;

import com.xoso.infrastructure.core.exception.AbstractPlatformException;
import com.xoso.infrastructure.security.domain.PlatformUser;
import com.xoso.infrastructure.security.service.AuthenticationService;
import com.xoso.user.model.AppUser;
import com.xoso.user.repository.AppUserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Service;

@Service
public class AuthenticationServiceImpl implements AuthenticationService {

    @Autowired
    private AppUserRepository appUserRepository;

    @Override
    public AppUser authenticatedUser() {

        AppUser currentUser = null;
        final SecurityContext context = SecurityContextHolder.getContext();
        if (context != null) {
            final Authentication auth = context.getAuthentication();
            if (auth != null) {
                if(auth.getPrincipal() instanceof User) {
                    var userDetail = (User) auth.getPrincipal();
                    currentUser = this.appUserRepository.findAppUserByName(userDetail.getUsername());
                } else if( auth.getPrincipal() instanceof  AppUser)
                    currentUser = (AppUser) auth.getPrincipal();
            }
        }
        if (currentUser == null) { throw new AbstractPlatformException("error.msg.auth.user.not.found", "User not found"); }
        return currentUser;
    }

}
