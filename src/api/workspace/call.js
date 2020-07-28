import http from 'api/gateway'

/**
 * 토큰 생성
 * @param {String} session // Session ID
 * @param {String} role // SUBSCRIBER, PUBLISHER(default), MODERATOR
 * @param {String} data // user data?
 * @param {String} kurentoOptions // optionsal JSON object
 */
export const getToken = async function({
  sessionId,
  role,
  data,
  kurentoOptions,
}) {
  const returnVal = await http('GET_TOKEN', {
    sessionId,
    role,
    data,
    kurentoOptions,
  })

  return returnVal
}

/**
 * 원격협업 멤버 내보내기
 * @query {String} sessionId
 * @query {String} workspaceId
 * @param {String} leaderId
 * @param {String} participantId
 */
export const kickoutMember = async ({
  sessionId,
  workspaceId,
  leaderId,
  participantId,
}) => {
  const returnVal = await http('KICKOUT_MEMBER', {
    sessionId,
    workspaceId,
    leaderId,
    participantId,
  })

  return returnVal
}

/**
 * 파일서버에 파일 전송
 *
 * @param {File}   file 전송할 파일 객체
 * @param {String} sessionId 파일이 전송된 협업 방 id
 * @param {String} workspaceId 파일이 전송된 워크스테이션 id
 */
export const sendFile = async ({ file, sessionId, workspaceId }) => {
  const returnVal = await http('SEND_FILE', { file, sessionId, workspaceId })
  return returnVal
}
