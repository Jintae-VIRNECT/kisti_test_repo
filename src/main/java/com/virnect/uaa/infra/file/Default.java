package com.virnect.uaa.infra.file;

/**
 * @author jeonghyeon.chang (johnmark)
 * @project PF-User
 * @email practice1356@gmail.com
 * @description
 * @since 2020.04.28
 */
public enum Default {
	USER_PROFILE("default");

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
