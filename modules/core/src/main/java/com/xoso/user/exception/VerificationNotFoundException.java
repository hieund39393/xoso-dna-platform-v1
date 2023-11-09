package com.xoso.user.exception;

import com.xoso.infrastructure.core.exception.AbstractPlatformException;

public class VerificationNotFoundException extends AbstractPlatformException {

    public VerificationNotFoundException() {
        super("error.user.verification.notFound", "Verification not found!");
    }
}
