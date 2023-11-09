package com.xoso.user.exception;

import com.xoso.infrastructure.core.exception.AbstractAppException;
import com.xoso.infrastructure.core.exception.AbstractPlatformException;
import com.xoso.infrastructure.core.exception.ExceptionCode;

public class CustomLockedException extends AbstractAppException {
    public CustomLockedException() {
        super(ExceptionCode.USER_IS_LOCKED);
    }
}
