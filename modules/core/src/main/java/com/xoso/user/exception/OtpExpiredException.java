package com.xoso.user.exception;

import com.xoso.infrastructure.core.exception.AbstractAppException;
import com.xoso.infrastructure.core.exception.AbstractPlatformException;
import com.xoso.infrastructure.core.exception.ExceptionCode;

public class OtpExpiredException extends AbstractAppException {

    public OtpExpiredException() {
        super(ExceptionCode.ERROR_OTP_EXPIRED);
    }
}
