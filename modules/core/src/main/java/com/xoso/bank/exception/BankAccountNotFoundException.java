package com.xoso.bank.exception;

import com.xoso.infrastructure.core.exception.AbstractPlatformException;

public class BankAccountNotFoundException extends AbstractPlatformException {

    public BankAccountNotFoundException(Long bankId) {
        super("error.msg.bank.account.not.found", "Bank account not found", bankId);
    }
}
