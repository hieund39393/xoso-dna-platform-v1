package com.xoso.campaign.model;

public enum CategoryEnum {
    KM("KM"),
    BANNER("BANNER"),
    NOTIFICATION("NOTIFICATION");
    private final String code;

    public String getCode() {
        return code;
    }
    CategoryEnum(String code) {
        this.code = code;
    }
}
