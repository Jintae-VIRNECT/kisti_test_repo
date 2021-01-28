import http from 'api/gateway'

/**
 * 원격협업 초대
 * @query {String} sessionId
 * @query {String} workspaceId
 * @param {String} leaderId
 * @param {Array[Object]} participants { id, email }
 */
export const inviteRoom = async ({
  sessionId,
  workspaceId,
  leaderId,
  participants,
}) => {
  const returnVal = await http('INVITE_ROOM', {
    sessionId,
    workspaceId,
    leaderId,
    participants,
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
export const getMember = async function ({
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
export const kickMember = async function ({
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
