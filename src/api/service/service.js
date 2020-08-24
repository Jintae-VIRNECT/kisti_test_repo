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
}) {
  const returnVal = await http('MEMBER_LIST', {
    filter,
    page,
    size,
    sort,
    workspaceId,
  })
  return returnVal
}

/**
 * 멤버 리스트 조회
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
