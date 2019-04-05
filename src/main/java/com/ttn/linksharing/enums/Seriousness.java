package com.ttn.linksharing.enums;

public enum Seriousness {
    SERIOUS("Serious"),
    CASUAL("Casual"),
    VERY_SERIOUS("Very Serious");

    String value;
    Seriousness(String value){
        this.value=value;
    }

    public String getValue(){
        return value;
    }
}
