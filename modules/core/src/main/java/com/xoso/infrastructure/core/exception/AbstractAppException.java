package com.xoso.infrastructure.core.exception;

public class AbstractAppException extends RuntimeException {

    private final ExceptionCode code;
    private final Object[] args;
    public AbstractAppException(ExceptionCode code) {
        this.code = code;
        this.args = null;
    }

    public AbstractAppException(ExceptionCode code,final Object... args) {
        this.code = code;
        this.args = args;
    }

    public String getCode() {
        return code.getCode();
    }

    @Override
    public String getMessage() {
        return  String.format(code.getLoMsg(), args);
    }

}
