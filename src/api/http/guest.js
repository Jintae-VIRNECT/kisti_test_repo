import http from 'api/gateway'

/**
 * Guest(seat) 멤버가 오픈방에 참여하기 위한 api
 *
 * @param {Object} joinRoomRequest {
    "uuid": "string",
    "memberType": "UNKNOWN",
    "deviceType": "MOBILE"
  }
 * @query {String} workspaceId 워크스페이스 id
 * @query {String} userId 유저 id
 * @returns {Object} 오픈방에 참여하기 위한 해당 협업 정보
 */
export const joinOpenRoomAsGuest = async function({
  joinRoomRequest,
  workspaceId,
  userId,
}) {
  const returnVal = await http('JOIN_AS_GUEST', {
    joinRoomRequest,
    workspaceId,
    userId,
  })
  return returnVal
}

/**
 * Guest 멤버를 초대하는 URL을 생성
 *
 * @query {String} workspaceId 워크스페이스 id
 * @query {String} userId 유저 id
 * @returns {Object} Guest 멤버 초대용 URL
 */
export const getGuestInviteUrl = async function({ sessionId, workspaceId }) {
  const returnVal = await http('GUEST_INVITE_URL', { sessionId, workspaceId })
  return returnVal
}

/**
 * Guest 정보 및 오픈 룸 정보를 반환
 *
 * @query {String} workspaceId
 * @query {String} userId
 * @returns {Object} 룸 정보
 */
export const getGuestRoomInfo = async function({ sessionId, workspaceId }) {
  const returnVal = await http('GUEST_ROOM_INFO', { sessionId, workspaceId })
  return returnVal
}
