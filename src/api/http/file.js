import http from 'api/gateway'

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
/**
 * 파일 업로드
 *
 * @param {File}   file
 * @param {String} sessionId
 * @param {String} workspaceId
 * @param {String} userId
 */
export const uploadFile = async ({ file, sessionId, workspaceId, userId }) => {
  const returnVal = await http('FILE_UPLOAD', {
    file,
    sessionId,
    workspaceId,
    userId,
  })
  return returnVal
}
/**
 * 파일 다운로드
 *
 * @param {Path} sessionId
 * @param {Path} workspaceId
 * @param {String} userId
 * @param {String} objectName
 */
export const downloadFile = async ({
  objectName,
  sessionId,
  workspaceId,
  userId,
}) => {
  const returnVal = await http('FILE_DOWNLOAD', {
    objectName,
    sessionId,
    workspaceId,
    userId,
  })
  return returnVal
}
/**
 * 파일 목록
 *
 * @param {Query} workspaceId
 * @param {Query} userId
 * @param {Query} fileName
 */
export const getFileList = async ({ sessionId, workspaceId, userId }) => {
  const returnVal = await http('FILE_LIST', {
    sessionId,
    workspaceId,
    userId,
  })
  return returnVal
}

/**
 * 로컬 녹화 파일 업로드
 *
 * @param {File}   file 대상 파일
 * @param {String} sessionId 세션 id
 * @param {String} workspaceId 워크스페이스 id
 * @param {String} userId 유저 id(업로드 유저 id)
 * @param {String} durationSec 영상 길이(초단위)
 */
export const uploadRecordFile = async ({
  file,
  sessionId,
  workspaceId,
  userId,
  durationSec,
}) => {
  const returnVal = await http('FILE_UPLOAD_RECORD', {
    file,
    sessionId,
    workspaceId,
    userId,
    durationSec,
  })
  return returnVal
}
