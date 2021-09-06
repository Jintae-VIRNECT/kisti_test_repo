package com.virnect.uaa.global.common;

public class MaskingUtil {

	/**
	 * 이메일 마스킹 처리
	 *
	 * @param email              - 이메일 정보
	 * @param maskingLength - 마스킹 문자열 수
	 * @return - 마스킹처리된 이메일 정보
	 */
	public static String emailMasking(final String email, int maskingLength) {
		String[] splitByAtSign = email.split("@");
		String emailID = splitByAtSign[0];
		String emailDomain = splitByAtSign[1];

		int emailIDMaskingLength = emailID.length() > maskingLength ? maskingLength : emailID.length() / 2;
		int emailDomainMaskingLength =
			emailDomain.length() > maskingLength ? maskingLength : emailDomain.length() / 2;

		String maskedEmail = masking(emailID, emailIDMaskingLength) + "@" + masking(emailDomain, emailDomainMaskingLength);
		return maskedEmail;
	}

	/**
	 * 문자열 마스킹 처리
	 * @param target - 마스킹 대상 문자열
	 * @param maskingLength - 마스킹 문자열 수
	 * @return
	 */
	public static String masking(String target, int maskingLength) {
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < target.length(); i++) {
			if (i < maskingLength) {
				sb.append(target.charAt(i));
			} else {
				sb.append("*");
			}
		}
		return sb.toString();
	}
}
