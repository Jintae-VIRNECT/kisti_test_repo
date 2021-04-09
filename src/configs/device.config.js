/**
 * 디바이스 관련 설정 값 정의
 */

/**
 * 카메라 상태값 정의
 */
export const CAMERA = {
  CAMERA_OFF: 0, // 카메라 꺼짐
  CAMERA_ON: 1, // 카메라 켜짐
  CAMERA_ZOOMING: 2, // 카메라 제어중
  CAMERA_NONE: 3, // 카메라 없음
  NO_PERMISSION: 4, // 권한 없음
  APP_IS_BACKGROUND: 5, // 앱 백그라운드 전환
}

/**
 * 플래시 상태값 정의
 */
export const FLASH = {
  FLASH_OFF: 0, // 플래시 켜짐
  FLASH_ON: 1, // 플래시 꺼짐
  CAMERA_ZOOMING: 2, // 카메라 제어중
  FLASH_NONE: 3, // 플래시 없음
  NO_PERMISSION: 4, // 권한 없음
  APP_IS_BACKGROUND: 5, // 앱 백그라운드 전환
}

/**
 * 참가자 디바이스 정의
 */
export const DEVICE = {
  WEB: 'DESKTOP', //데스크탑
  MOBILE: 'MOBILE', //모바일
  GLASSES: 'SMART_GLASSES', //스마트 글래스
  HOLOLENS: 'HOLOLENS', //홀로렌즈
  UNKNOWN: 'UNKNOWN', //미정
  FITT360: 'FITT360', //fitt360 앱
}
