import http from 'api/gateway'

/**
 * 서버 녹화 시작 요청
 * @query {String} workspaceId 워크스페이스 uuid
 * @query {String} userId 유저 uuid
 * @param {Number} framerate 영상 프레임 레이트
 * @param {String} recordingFilename 파일 명
 * @param {Number} recordingTimeLimit 녹화 시간
 * @param {String} resolution 해상도
 * @param {String} sessionId 녹화대상 세션 Id (openvidu 세션 id)
 * @param {String} token 토큰값
 * @param {Object} metaData 메타 데이터
 */
export const startServerRecord = async ({
  workspaceId,
  userId,
  framerate = 20,
  recordingFilename,
  recordingTimeLimit = 5,
  resolution = '720p',
  sessionId,
  token,
  metaData = {},
}) => {
  const returnVal = await http('START_SERVER_RECORD', {
    workspaceId,
    userId,
    framerate,
    recordingFilename,
    recordingTimeLimit,
    resolution,
    sessionId,
    token,
    metaData,
  })
  return returnVal
}

/**
 * 서버 녹화 중단 요청
 * @query {String} workspaceId 워크스페이스 uuid
 * @query {String} userId 유저 uuid
 * @query {String} id 중단할 녹화 세션 id
 */
export const stopServerRecord = async ({ workspaceId, userId, id }) => {
  const returnVal = await http('STOP_SERVER_RECORD', {
    workspaceId,
    userId,
    id,
  })
  return returnVal
}

/**
 * 현재 특정 세션에서 진행중인 서버 녹화 목록 반환
 * @query {*} workspaceId
 * @query {*} userId
 * @param {*} sessionId
 */
export const getServerRecordList = async ({
  workspaceId,
  userId,
  sessionId,
}) => {
  const returnVal = await http('SERVER_RECORD_LIST', {
    workspaceId,
    userId,
    sessionId,
  })
  return returnVal
}
