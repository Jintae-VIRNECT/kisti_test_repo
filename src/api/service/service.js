import http from 'api/gateway'

/**
 * 원격협업 초대
 * @query {String} sessionId
 * @query {String} workspaceId
 * @param {String} leaderId
 * @param {Array[Object]} participants
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
