module.exports = {
	// 로그인
	POST_SIGHIN: ['POST', '/auth/signin'],
	// 회원가입
	POST_SIGHUP: ['POST', '/auth/signup'],
	// 가입 - 이메일 인증
	POST_EMAIL_AUTH: ['POST', '/auth/email'],
	// 가입 - 메일 인증 코드
	GET_VERIFICATION: ['GET', '/auth/verification'],
	// QR코드 로그인 센터
	POST_OTP_QR: ['POST', '/auth/otp/qr'],

	// 유저 상세정보
	GET_USER_INFO: ['GET', '/users/{userID}'],
	// 이메일로 찾기
	POST_FIND_EMAIL: ['POST', '/users/find/email'],
	// 비밀번호 재설정 - 이메일 코드 발송
	POST_FIND_PASS: ['POST', '/users/find/password/auth'],
	// 비밀번호 재설정 - 코드 체크
	POST_CODE_CHECK: ['POST', '/users/find/password/check'],
	// 비밀번호 재설정 - 비번 재설정
	PUT_PASS: ['PUT', '/users/find/password'],
}
