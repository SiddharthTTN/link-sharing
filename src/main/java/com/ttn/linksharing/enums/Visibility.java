package com.ttn.linksharing.enums;

public enum Visibility {
    PRIVATE("Private"),
    PUBLIC("Public");
    String value;

    Visibility(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
