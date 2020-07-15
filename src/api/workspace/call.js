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
 * 파일서버에 파일 전송
 * @param {File} file 전송할 파일 객체
 * @param {String} roomId 파일이 전송된 협업 방 id
 * @param {String} workspaceId 파일이 전송된 워크스테이션 id
 */
export const sendFile = async (file, roomId, workspaceId) => {
  const returnVal = await http('SEND_FILE', { file, roomId, workspaceId })
  return returnVal
}
