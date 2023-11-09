package com.xoso.lottery.model;

public enum SessionStatus {
    WAITING("WAITTING"),
    PROCESSING("PROCESSING"),

    DONE("DONE");
    private final String code;

    public String getCode() {
        return code;
    }

    SessionStatus(String code) {
        this.code = code;
    }
}
