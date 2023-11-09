package com.xoso.api.exception;

import com.xoso.infrastructure.core.exception.AbstractPlatformException;

public class InvalidValueException extends AbstractPlatformException {


    public InvalidValueException() {
        super("error.msg.value.invalid", "Invalid value!");
    }

    public InvalidValueException(String field) {
        super("error.msg.value.invalid." + field, "Invalid value for " + field, field);
    }

    public InvalidValueException(String field, String message) {
        super("error.api.value.invalid." + field, message, field);
    }
}
