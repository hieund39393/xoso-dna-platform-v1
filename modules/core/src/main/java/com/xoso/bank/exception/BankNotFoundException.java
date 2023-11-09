package com.xoso.bank.exception;

import com.xoso.infrastructure.core.exception.AbstractAppException;
import com.xoso.infrastructure.core.exception.AbstractPlatformException;
import com.xoso.infrastructure.core.exception.ExceptionCode;

public class BankNotFoundException extends AbstractAppException {

    public BankNotFoundException(Long bankId) {
        super(ExceptionCode.BANK_NOT_FOUND);
    }
}
