package com.xoso.wallet.model;

public enum TransactionType {

    WITHDRAW("WITHDRAW"),
    DEPOSIT("DEPOSIT");

    private final String code;

    public String getCode() {
        return code;
    }

    TransactionType(String code) {
        this.code = code;
    }
}
