package com.xoso.api.security.service.impl;

import com.nimbusds.jose.util.Base64;
import com.xoso.api.security.dao.DaoAuthenticationProvider;
import com.xoso.api.security.data.AuthenticatedUserData;
import com.xoso.api.security.exception.TokenInvalidException;
import com.xoso.api.security.service.AuthenticateService;
import com.xoso.infrastructure.security.service.JwtService;
import com.xoso.user.data.RoleData;
import com.xoso.user.exception.UserNotFoundException;
import com.xoso.user.model.AppUser;
import com.xoso.user.model.Role;
import com.xoso.user.repository.AppUserRepository;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Set;

@Service
public class AuthenticateServiceImpl implements AuthenticateService {

    private final JwtService jwtService;
    private final DaoAuthenticationProvider customAuthenticationProvider;
    private final AppUserRepository appUserRepository;

    @Autowired
    public AuthenticateServiceImpl(JwtService jwtService, DaoAuthenticationProvider customAuthenticationProvider, AppUserRepository appUserRepository) {
        this.jwtService = jwtService;
        this.customAuthenticationProvider = customAuthenticationProvider;
        this.appUserRepository = appUserRepository;
    }

    @Override
    public AuthenticatedUserData login(String username, String password) {
        final var authentication = new UsernamePasswordAuthenticationToken(username, password);
        final var authenticationCheck = this.customAuthenticationProvider.authenticate(authentication);
        var appUser = this.appUserRepository.findAppUserByName(username);
        if (appUser == null) {
            throw new UserNotFoundException();
        }
        var token = jwtService.generateTokenLoginFromUsername(appUser);
        var refreshToken = jwtService.generateRefreshToken(appUser);
        final byte[] base64EncodedAuthenticationKey = Base64.encode(token).decode();
        final byte[] base64EncodedRefreshToken = Base64.encode(refreshToken).decode();
        return this.checkAndRespond(authenticationCheck, username, base64EncodedAuthenticationKey, base64EncodedRefreshToken);
    }

    @Override
    public AuthenticatedUserData refreshToken(String refreshToken) {
        try {
            if (StringUtils.isNotBlank(refreshToken) && jwtService.validateToken(refreshToken)) {
                var username = jwtService.getUsernameFromToken(refreshToken);
                var appUser = this.appUserRepository.findAppUserByName(username);
                var accessToken = jwtService.generateTokenLoginFromUsername(appUser);
                final byte[] base64EncodedAuthenticationKey = Base64.encode(accessToken).decode();

                var roles = new ArrayList<RoleData>();
                var userRoles = appUser.getRoles();
                for (final Role role : userRoles) {
                    roles.add(role.toData());
                }

                var authenticatedUserData = new AuthenticatedUserData(username, appUser.getId(), new String(base64EncodedAuthenticationKey, StandardCharsets.UTF_8),
                        null , null, roles, null);
                authenticatedUserData.setFullName(appUser.getFullName());
                authenticatedUserData.setBase64EncodedRefreshToken(refreshToken);
                return authenticatedUserData;
            }
            return null;
        } catch (Exception e) {
            e.printStackTrace();
            throw new TokenInvalidException();
        }
    }

    protected AuthenticatedUserData checkAndRespond(Authentication authenticationCheck, String username,
                                                    byte[] base64EncodedAuthenticationKey, byte[] base64EncodedRefreshToken) {
        final Collection<String> permissions = new ArrayList<>();
        var authenticatedUserData = new AuthenticatedUserData(username, permissions);
        if (authenticationCheck.isAuthenticated()) {
            final Collection<GrantedAuthority> authorities = new ArrayList<>(authenticationCheck.getAuthorities());
            for (final GrantedAuthority grantedAuthority : authorities) {
                permissions.add(grantedAuthority.getAuthority());
            }

            final AppUser principal = (AppUser) authenticationCheck.getPrincipal();
            final Collection<RoleData> roles = new ArrayList<>();
            final Set<Role> userRoles = principal.getRoles();
            for (final Role role : userRoles) {
                roles.add(role.toData());
            }

            final Long staffId = principal.getStaffId();
            final String staffDisplayName = principal.getStaffDisplayName();

            authenticatedUserData = new AuthenticatedUserData(username, principal.getId(), new String(base64EncodedAuthenticationKey, StandardCharsets.UTF_8),
                    staffId , staffDisplayName, roles, permissions);
            authenticatedUserData.setFullName(principal.getFullName());
            authenticatedUserData.setBase64EncodedRefreshToken(new String(base64EncodedRefreshToken, StandardCharsets.UTF_8));
        }
        return authenticatedUserData;
    }

}
