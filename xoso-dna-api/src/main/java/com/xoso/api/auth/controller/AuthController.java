package com.xoso.api.auth.controller;

import com.xoso.api.auth.wsdto.LoginWsDTO;
import com.xoso.api.auth.wsdto.RefreshTokenWsDTO;
import com.xoso.api.controller.BaseController;
import com.xoso.api.security.exception.TokenInvalidException;
import com.xoso.api.security.service.AuthenticateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
public class AuthController extends BaseController {

    private final AuthenticateService authenticateService;

    @Autowired
    public AuthController(AuthenticateService authenticateService) {
        this.authenticateService = authenticateService;
    }

    @PostMapping(value = "/login")
    public ResponseEntity<?> login(@RequestBody LoginWsDTO request) {
        var data = this.authenticateService.login(request.getUsername(), request.getPassword());
        return response(data);
    }

    @PostMapping(value = "/refresh-token")
    public ResponseEntity<?> refreshToken(@RequestBody RefreshTokenWsDTO request) {
        var authenticatedUserData = this.authenticateService.refreshToken(request.getRefreshToken());
        if (authenticatedUserData == null) {
            throw new TokenInvalidException();
        }
        return response(authenticatedUserData);
    }
}
