package com.virnect.data.domain.session;

import java.util.HashMap;
import java.util.Map;

public enum SessionType {
    PRIVATE, // 비 공개형 원격협업
    PUBLIC,  // 공개형 원격협업
    OPEN;    // 오픈 원격협업

    private static final Map<String, SessionType> enumString = new HashMap<>();
    static {
        for (SessionType sessionType: values()) {
            enumString.put(sessionType.name(), sessionType);
        }
    }

    public static SessionType findBy(String s) {
        return enumString.get(s.toUpperCase());
    }
}
