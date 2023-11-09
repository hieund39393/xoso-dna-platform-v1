package com.xoso.wallet.exception;


import com.xoso.infrastructure.core.exception.AbstractPlatformException;

public class WalletTransactionNotFountException extends AbstractPlatformException {

    public WalletTransactionNotFountException() {
        super("error.msg.wallet.transaction.notFound", "Wallet transaction not found");
    }
}
