package com.xoso.lottery.model;

public enum TypeCode {
    XSN("XSN"),
    XSCHANLE("XSCHANLE"),
    XSCT("XSCT"),
    XSB("XSB");
    private final String code;

    public String getCode() {
        return code;
    }

    TypeCode(String code) {
        this.code = code;
    }
}
