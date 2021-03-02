package com.virnect.remote.dto.constraint;

public enum TranslationItem {
	LANGUAGE_KR("한국어", "ko-KR"),
	LANGUAGE_EN("English", "en-US"),
	LANGUAGE_JP("日本語", "ja-JP"),
	LANGUAGE_ZH("中文", "zh-CN"),
	LANGUAGE_FR("française","fr-FR"),
	LANGUAGE_ES("Español","es-ES"),
	LANGUAGE_RU("русский","ru-RU"),
	LANGUAGE_UK("Український","uk-UA"),
	LANGUAGE_PL("Polskie","pl-PL");

	private final String language;
	private final String languageCode;

	TranslationItem(final String language, final String languageCode) {
		this.language = language;
		this.languageCode = languageCode;
	}

	public String getLanguage() {
		return language;
	}

	public String getLanguageCode() {
		return languageCode;
	}
}