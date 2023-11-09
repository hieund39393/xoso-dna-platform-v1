package com.xoso.wallet.exception;


import com.xoso.infrastructure.core.exception.AbstractAppException;
import com.xoso.infrastructure.core.exception.AbstractPlatformException;
import com.xoso.infrastructure.core.exception.ExceptionCode;

public class PasswordWithdrawDoesNotMatchException extends AbstractAppException {

    public PasswordWithdrawDoesNotMatchException() {
        super(ExceptionCode.PASSWORD_WITHDRAW_NOT_MATCH);
    }
}
