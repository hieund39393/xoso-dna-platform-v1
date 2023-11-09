package com.xoso.lottery.model;

public enum ModeCode {
    DDB("DDB"),
    BCDB("BCDB"),
    DGN("DGN"),
    BCGN("BCGN"),
    LO("LO"),
    LX2("LX2"),
    LX3("LX3"),
    LX4("LX4"),
    CHANLE("CHANLE");
    private final String code;

    public String getCode() {
        return code;
    }

    ModeCode(String code) {
        this.code = code;
    }
}
