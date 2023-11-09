package com.xoso.wallet.model;

public enum WalletStatus {
    ACTIVE("ACTIVE"),
    INACTIVE("INACTIVE");

    private final String code;

    public String getCode() {
        return code;
    }

    WalletStatus(String code) {
        this.code = code;
    }
}
