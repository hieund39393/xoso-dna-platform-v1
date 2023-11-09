package com.xoso.user.exception;

import com.xoso.infrastructure.core.exception.AbstractAppException;
import com.xoso.infrastructure.core.exception.AbstractPlatformException;
import com.xoso.infrastructure.core.exception.ExceptionCode;

public class UserAlreadyExistsException extends AbstractAppException {

    public UserAlreadyExistsException() {
        super(ExceptionCode.USER_ALREADY_EXISTED);
    }
}
