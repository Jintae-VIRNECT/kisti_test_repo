module.exports = {
  // 사용자 정보 리스트 조회
  GET_USERS: ['GET', '/users'],
  // 사용자 정보 조회
  GET_USER_INFO: ['GET', '/users/{userId}'],
  // 개인 정보 수정 api
  UPDATE_USER_INFO: ['POST', '/users/{userId}'],
  // 사용자 프로필 업데이트
  UPDATE_USER_IMAGE: ['POST', '/users/{userId}/profile'],
  // 개인 정보 접근 인증
  ACCESS_AUTH: ['POST', '/users/{userId}/access'],
  // 사용자 정보 조회(authorization 토큰을 전달할 시 사용)
  GET_AUTH_INFO: ['GET', '/users/info'],
  // 내 쿠폰 정보 리스트 조회
  GET_COUPONS: ['GET', '/licenses/coupon/{userId}'],
}
