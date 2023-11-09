package com.xoso.client.model;

public enum ClientStatus {
    INVALID("INVALID"),
    PENDING("PENDING"),
    ACTIVE("ACTIVE"),
    CLOSED("CLOSED"),
    REJECTED("REJECTED");

    private final String code;

    public String getCode() {
        return code;
    }

    ClientStatus(String code) {
        this.code = code;
    }
}
