import http from 'api/gateway'

/**
 * 원격협업 초대
 * @query {String} sessionId
 * @query {String} workspaceId
 * @param {String} leaderId
 * @param {Array[String]} participantIds
 */
export const inviteRoom = async ({
  sessionId,
  workspaceId,
  leaderId,
  participantIds = [],
}) => {
  const returnVal = await http('INVITE_ROOM', {
    sessionId,
    workspaceId,
    leaderId,
    participantIds,
  })

  return returnVal
}

/**
 * 멤버 리스트 조회
 * @query {String} workspaceId
 * @param {String} filter
 * @param {Number} page
 * @param {Number} size
 * @param {String} sort
 */
export const getMember = async function({
  filter = '',
  page = 0,
  size = 10,
  sort = 'email,desc',
  workspaceId,
  userId,
}) {
  const returnVal = await http('MEMBER_LIST', {
    filter,
    page,
    size,
    sort,
    workspaceId,
    userId,
  })
  return returnVal
}

/**
 * 멤버 내보내기
 * @query {String} sessionId
 * @query {String} workspaceId
 * @param {String} leaderId
 * @param {String} participantId
 */
export const kickMember = async function({
  sessionId,
  workspaceId,
  leaderId,
  participantId,
}) {
  const returnVal = await http('KICKOUT_MEMBER', {
    sessionId,
    workspaceId,
    leaderId,
    participantId,
  })
  return returnVal
}

/**
 * 통화중 신호 전송
 * @query {String} workspaceId
 * @param {String} sessionId
 * @param {Array[String]} to
 * @param {String} type
 * @param {String} data
 */
export const sendSignal = async function({ sessionId, to, type, data }) {
  const returnVal = await http('SEND_SIGNAL', {
    sessionId,
    to,
    type,
    data: JSON.stringify(data),
  })
  return returnVal
}

/**
 * 초대 가능 멤버 리스트
 * @param {String} workspaceId
 * @param {String} sessionId
 * @param {String} userId
 * @param {String} filter
 * @param {String} page
 * @param {String} size
 * @param {String} sort
 */
export const invitableList = async function({
  filter = '',
  page = 0,
  size = 50,
  sort = 'role,desc',
  workspaceId,
  sessionId,
  userId,
}) {
  const returnVal = await http('INVITABLE_MEMBER_LIST', {
    filter,
    page,
    size,
    sort,
    workspaceId,
    sessionId,
    userId,
  })
  return returnVal
}
