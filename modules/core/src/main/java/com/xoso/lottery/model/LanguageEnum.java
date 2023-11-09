package com.xoso.lottery.model;

public enum LanguageEnum {
    VI("vi"),
    THA("tha"),
    LAO("lao"),
    CAM("cam");
    private final String code;

    public String getCode() {
        return code;
    }
    LanguageEnum(String code) {
        this.code = code;
    }
}
