package com.xoso.wallet.exception;

import com.xoso.infrastructure.core.exception.AbstractPlatformException;
public class WalletIsNotMaster extends AbstractPlatformException {
    public WalletIsNotMaster() {
        super("error.msg.wallet.is.not.master", "Wallet is not master");
    }
}
