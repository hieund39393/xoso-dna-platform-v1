package com.xoso.api.exception;

import com.xoso.infrastructure.core.exception.AbstractAppException;
import com.xoso.infrastructure.core.exception.AbstractPlatformException;
import com.xoso.infrastructure.core.exception.ExceptionCode;

public class InvalidUsernameOrPasswordException extends AbstractAppException {
    public InvalidUsernameOrPasswordException() {
        super(ExceptionCode.USERNAME_PASSWORD_INVALID);
    }

}
