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
  CONNECTION_ESTABLISHED: 'Connection Establish.',
  REGISTER_SUCCESS: 'Register Success',
  REGISTER_FAIL: 'Register Fail.',
}
