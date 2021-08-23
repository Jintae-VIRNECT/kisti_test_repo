package com.virnect.content.domain;

/**
 * @author hangkee.min (henry)
 * @project PF-ContentManagement
 * @email hkmin@virnect.com
 * @description
 * @since 2020.04.08
 */
public enum TargetType {
	QR("QR"),
	VTarget("VTarget"),
	IMAGE("IMAGE");

	private String message;

	TargetType(String message) {
		this.message = message;
	}

	public String getMessage() {
		return message;
	}
}

