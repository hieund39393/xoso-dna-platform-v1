package com.xoso.infrastructure.core.exception;

import org.springframework.http.HttpStatus;

public class PermissionDeniedException extends AbstractPlatformException{
    public PermissionDeniedException(String message, Object... args) {
        super(HttpStatus.FORBIDDEN.name(),  String.format(message, args), null);
    }
}
