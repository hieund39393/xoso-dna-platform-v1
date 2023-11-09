package com.xoso.client.model;

public enum GenderEnum {
    FEMALE("FEMALE"),
    MALE("MALE");

    private final String code;

    public String getCode() {
        return code;
    }
    GenderEnum(String code) {
        this.code = code;
    }
}
