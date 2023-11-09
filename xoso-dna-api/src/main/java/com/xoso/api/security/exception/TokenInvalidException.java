package com.xoso.api.security.exception;

import com.xoso.infrastructure.core.exception.AbstractPlatformException;

public class TokenInvalidException extends AbstractPlatformException {


    public TokenInvalidException() {
        super("error.msg.security.token.invalid", "Token invalid");
    }
}
