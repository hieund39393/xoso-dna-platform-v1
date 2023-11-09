package com.xoso.lottery.data;

public enum LotteryStatus {
    WAITING("WAITTING"),
    PROCESSING("PROCESSING"),

    DONE("DONE");
    private final String code;

    public String getCode() {
        return code;
    }

    LotteryStatus(String code) {
        this.code = code;
    }
}
