package com.virnect.content.global.util;

import org.slf4j.MDC;

public class CurrentUserUtils {
	public static String getUserUUID() {
		return MDC.get("userUUID");
	}
}
