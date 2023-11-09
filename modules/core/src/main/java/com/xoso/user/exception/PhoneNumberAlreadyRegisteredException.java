package com.xoso.user.exception;

import com.xoso.infrastructure.core.exception.AbstractAppException;
import com.xoso.infrastructure.core.exception.AbstractPlatformException;
import com.xoso.infrastructure.core.exception.ExceptionCode;

public class PhoneNumberAlreadyRegisteredException extends AbstractAppException {
    public PhoneNumberAlreadyRegisteredException() {
        super(ExceptionCode.PHONE_ALREADY_REGISTED);
    }
}
