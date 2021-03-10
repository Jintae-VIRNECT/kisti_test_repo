package com.virnect.data.domain.room;
import java.util.HashMap;
import java.util.Map;

public enum RestrictedMode {

	ON, // 카메라 OFF (Default)
	OFF;  // 카메라 ON

	private static final Map<String, RestrictedMode> enumString = new HashMap<>();
	static {
		for (RestrictedMode restrictedMode : values()) {
			enumString.put(restrictedMode.name(), restrictedMode);
		}
	}
	public static RestrictedMode findBy(String s) {
		return enumString.get(s.toUpperCase());
	}

}
