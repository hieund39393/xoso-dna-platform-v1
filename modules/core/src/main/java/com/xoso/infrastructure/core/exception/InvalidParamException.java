package com.xoso.infrastructure.core.exception;

import org.springframework.http.HttpStatus;

public class InvalidParamException extends AbstractPlatformException {
    public InvalidParamException(Object... args) {
        super("error.invalid.param", "Invalid param", args);
    }
}
