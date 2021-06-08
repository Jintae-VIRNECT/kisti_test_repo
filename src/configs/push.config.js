/**
 * 메시지 서버에서 클라이언트로 전송되는 이벤트 관련 값 정의
 * @see https://www.notion.so/virnect/Remote-2-0-Notification-Server-Message-e2a87653711c4782808e38b3b5f16820
 */

/**
 * 메시지 서버 경로
 */
export const DESTINATION = {
  PUSH: '/topic/push',
  FORCELOGOUT: '/topic/event.force_logout',
}

/**
 * 메시지 서버 키값
 */
export const KEY = {
  SERVICE_TYPE: 'remote',
}

/**
 * 메시지 서버에서 오는 이벤트 정의
 */
export const EVENT = {
  LICENSE_EXPIRATION: 'licenseExpiration', //라이센스 시간 만료 임박
  LICENSE_EXPIRED: 'licenseExpired', //라이센스 만료
  INVITE: 'invite', //협업 초대
  INVITE_DENIED: 'invitationDenied', //협업 초대 거부
  INVITE_ACCEPTED: 'invitationAccepted', //협업 초대 승인
}

/**
 * 인증서버에 전송하는 command
 */
export const COMMAND = {
  REGISTER: 'REGISTER',
  REMOTE_EXIT: 'REMOTE_EXIT',
  WORKSPACE_UPDATE: 'WORKSPACE_UPDATE',
}
