package com.xoso.wallet.exception;

import com.xoso.infrastructure.core.exception.AbstractPlatformException;

public class ExistTransactionNotCompleted extends AbstractPlatformException {
    public ExistTransactionNotCompleted() {
        super("error.msg.wallet.exist.transaction.not.complete", "Exist transaction not completed");
    }
}
