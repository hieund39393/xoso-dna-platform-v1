package com.xoso.infrastructure.core.exception;

public class AbstractPlatformException extends RuntimeException {

    private final String code;
    private final String message;
    private final Object[] args;

    public AbstractPlatformException(final String code, final String message, final Object... args) {
        this.code = code;
        this.message = message;
        this.args = args;
    }

    public String getCode() {
        return code;
    }

    @Override
    public String getMessage() {
        return message;
    }

    public Object[] getArgs() {
        return args;
    }
}
