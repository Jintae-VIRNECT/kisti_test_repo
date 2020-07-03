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
  // 내가 속한 워크스페이스 목록 조회
  GET_WORKSPACES: ['GET', '/workspaces'],
  // 내 쿠폰 정보 리스트 조회
  GET_COUPONS: ['GET', '/licenses/coupon/{userId}'],
  // 쿠폰 등록
  ADD_COUPON: ['POST', '/licenses/coupon/register'],
  // 쿠폰 사용
  USE_COUPON: ['PUT', '/licenses/coupon/active'],
  // 워크스페이스 플랜 사용자 조회
  GET_LICENSE_MEMBERS: ['GET', '/workspaces/{workspaceId}/members/license'],
  /**
   * 페이레터 API
   */
  // 결제 이력
  GET_PAYMENT_LOGS: ['GET', '/billing/user/paymentlist'],
  // 정기결제 정보 조회
  GET_AUTO_PAYMENTS: ['GET', '/billing/user/purchaseinfo'],
  // 정기결제 해지 신청
  CANCEL_AUTO_PAYMENTS: ['POST', '/billing/user/monthpaycnl'],
  // 정기결제 해지 신청 취소
  CANCEL_AUTO_PAYMENTS_ABORT: ['POST', '/billing/user/monthpaycnl/reversal'],
  // 이용자 구매내역 상세 조회
  GET_PAYMENT_LOG_DETAIL: ['GET', '/billing/user/purchaselist'],
  // 페이레터 쿠폰 정보 리스트 조회
  GET_PAY_COUPONS: ['GET', '/billing/coupon/holdlist'],
}
