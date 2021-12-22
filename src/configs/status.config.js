/**
 * 상태값 관련 정의
 */

/**
 * 협업 방 내부에 참가되어 있는지 여부 정의
 */
export const STATUS = {
  UNLOAD: 'UNLOAD',
  LOAD: 'LOAD', //대상 유저가 협업 내에 접속되어 있는 상태
}

/**
 * 워크스페이스에 부여된 역할 정의
 */
export const WORKSPACE_ROLE = {
  MASTER: 'MASTER',
  MANAGER: 'MANAGER',
  MEMBER: 'MEMBER',
}

/**
 * 계정의 플랜 상태 정의
 */
export const PLAN_STATUS = {
  ACTIVE: 'ACTIVE', //정상
  INACTIVE: 'INACTIVE', //만료
  EXCEEDED: 'EXCEEDED', //할당된 플랜 초과
}

/**
 * 협업 유형 정의
 */
export const ROOM_STATUS = {
  PUBLIC: 'PUBLIC', //TBD
  PRIVATE: 'PRIVATE', //원격 협업
  OPEN: 'OPEN', //오픈방
}

/**
 * 인증 서버 연결 메시지
 */
export const AUTH_STATUS = {
  CONNECT_SUCCESS: 200,
  REGISTRATION_SUCCESS: 300,
  REGISTRATION_FAIL: 301,
  DUPLICATED_REGISTRATION: 302, //REGISTER 요청에 대한 중복 로그인 에러

  REMOTE_EXIT_REQ_SUCCESS: 400,
  REMOTE_EXIT_REQ_FAIL_NOT_FOUND: 402,
  REMOTE_EXIT_RECEIVED: 401,

  WORKSPACE_UPDATE_SUCCESS: 600, //워크스페이스 변경  성공
  WORKSPACE_UPDATE_DUPLICATED: 601, //워크스페이스 변경 요청에 대한 중복로그인 에러
  WORKSPACE_UPDATE_FAIL: 602, //워크스페이스 변경 실패

  FORCE_LOGOUT_RECEIVED: 500, //강제로그아웃 메시지 수신
}

/**
 * 멤버 상태
 */
export const MEMBER_STATUS = ['login', 'join', 'logout']

/**
 * 카메라 상태
 */
export const CAMERA_STATE = {
  BACKGROUND: 'background',
  ON: 'on',
  OFF: 'off',
  UNAVAILABLE: -1,
}
