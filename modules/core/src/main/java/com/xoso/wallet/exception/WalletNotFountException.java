package com.xoso.wallet.exception;


import com.xoso.infrastructure.core.exception.AbstractPlatformException;

public class WalletNotFountException extends AbstractPlatformException {

    public WalletNotFountException() {
        super("error.msg.wallet.notFound", "Wallet not found");
    }

    public WalletNotFountException(String userName) {
        super("error.msg.wallet.notFound", String.format("Wallet [%s] not found",userName));
    }
}
