package com.xoso.wallet.model;

public enum WalletTransactionStatus {

    NEW("NEW"),
    SUCCESS("SUCCESS"),
    FAILED("FAILED"),
    CANCELED("CANCELED");

    private final String code;

    public String getCode() {
        return code;
    }

    WalletTransactionStatus(String code) {
        this.code = code;
    }
}
