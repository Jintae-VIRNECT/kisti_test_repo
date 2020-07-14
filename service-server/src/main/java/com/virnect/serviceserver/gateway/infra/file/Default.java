package com.virnect.serviceserver.gateway.infra.file;

public enum Default {
    ROOM_PROFILE("default");

    private String value;

    Default(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public boolean isValueEquals(String target) {
        return this.value.equals(target);
    }
}
