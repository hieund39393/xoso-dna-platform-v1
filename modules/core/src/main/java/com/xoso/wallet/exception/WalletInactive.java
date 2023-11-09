package com.xoso.wallet.exception;

import com.xoso.infrastructure.core.exception.AbstractPlatformException;

public class WalletInactive extends AbstractPlatformException {
    public WalletInactive() {
        super("error.msg.wallet.inactive", "Wallet inactive");
    }
}
