package com.xoso.wallet.exception;

import com.xoso.infrastructure.core.exception.AbstractPlatformException;
public class InvalidValueException extends AbstractPlatformException {

    public InvalidValueException(String field) {
        super("error.msg.value.invalid." + field, "Invalid value for " + field, field);
    }

    public InvalidValueException(String field, String mesage) {
        super("error.wallet.value.invalid." + field, mesage, field);
    }
}
