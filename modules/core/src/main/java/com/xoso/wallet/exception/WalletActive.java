package com.xoso.wallet.exception;

import com.xoso.infrastructure.core.exception.AbstractPlatformException;

public class WalletActive extends AbstractPlatformException {
    public WalletActive() {
        super("error.msg.wallet.active", "Wallet active");
    }
}
