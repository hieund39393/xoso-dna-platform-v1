package com.xoso.wallet.exception;

import com.xoso.infrastructure.core.exception.AbstractPlatformException;

public class DuplicateEntityException extends AbstractPlatformException {

    public DuplicateEntityException() {
        super("error.wallet.duplicate.entity", "Duplicate entity!");
    }
}
