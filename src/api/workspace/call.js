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
