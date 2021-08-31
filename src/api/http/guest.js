import http from 'api/gateway'

/**
 * Guest(seat) 멤버가 오픈방에 참여하기 위한 api
 *
 * @query {String} workspaceId 워크스페이스 id
 * @query {String} userId 유저 id
 * @returns {Object} 오픈방에 참여하기 위한 해당 협업 정보
 */
export const joinOpenRoomAsGuest = async function({
  uuid,
  memberType,
  deviceType,
  sessionId,
  workspaceId,
}) {
  const returnVal = await http('JOIN_AS_GUEST', {
    uuid,
    memberType,
    deviceType,
    sessionId,
    workspaceId,
  })
  return returnVal
}

/**
 * Guest 멤버 정보를 할당 받음
 * @param {String} workspaceId 워크스페이 id
 * @returns {Object} 할당된 seat 멤버 정보 {
  "code": 200,
  "data": {
    "accessToken": "string",
    "expireIn": 0,
    "name": "string",
    "nickname": "string",
    "refreshToken": "string",
    "uuid": "string",
    "workspaceId": "string"
  },
  "message": "success"
}
 */
export const getGuestInfo = async ({ workspaceId }) => {
  const returnVal = await http('GUEST_INFO', { workspaceId })
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
