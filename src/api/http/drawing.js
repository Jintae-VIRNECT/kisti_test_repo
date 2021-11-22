import http from 'api/gateway'

/**
 * 협업보드 파일 업로드
 * @param {File} file
 * @param {String} fileType
 * @param {String} sessionId
 * @param {String} userId
 * @param {String} workspaceId
 */
export const remoteFileUpload = async ({
  file,
  fileType,
  sessionId,
  userId,
  workspaceId,
}) => {
  const returnVal = await http('REMOTE_FILE_UPLOAD', {
    file,
    fileType,
    sessionId,
    userId,
    workspaceId,
  })
  return returnVal
}
/**
 * 협업보드 파일 목록
 * @query {Boolean} deleted
 * @query {Boolean} paging
 * @query {String} page
 * @query {String} size
 * @query {String} sort
 * @query {String} sessionId
 * @query {String} workspaceId
 * @param {String} fileType
 */
export const remoteFileList = async ({
  deleted = false,
  page = 0,
  size = 10,
  paging = false,
  sort = 'createdDate,DESC',
  sessionId,
  workspaceId,
  fileType,
}) => {
  const returnVal = await http('REMOTE_FILE_LIST', {
    deleted,
    page,
    paging,
    size,
    sort,
    sessionId,
    workspaceId,
    fileType,
  })
  return returnVal
}
/**
 * 협업보드 파일 다운로드
 * @path {String} sessionId
 * @path {String} workspaceId
 * @query {String} objectName
 * @query {String} userId
 */
export const remoteFileDownload = async ({
  sessionId,
  workspaceId,
  objectName,
  userId,
}) => {
  const returnVal = await http('REMOTE_FILE_DOWNLOAD', {
    sessionId,
    workspaceId,
    objectName,
    userId,
  })
  return returnVal
}
/**
 * 협업보드 공유 파일 삭제
 * @path {String} workspaceId
 * @path {String} sessionId
 * @query {String} leaderUserId
 * @query {String} objectName
 */
export const remoteFileRemove = async ({
  workspaceId,
  sessionId,
  leaderUserId,
  objectName,
}) => {
  const returnVal = await http('REMOTE_FILE_REMOVE', {
    workspaceId,
    sessionId,
    leaderUserId,
    objectName,
  })
  return returnVal
}

/**
 * 미사용
 * 협업보드 공유 파일 전체 삭제
 * @path {String} workspaceId
 * @path {String} sessionId
 * @query {String} userId
 */
// export const remoteFileRemoveAll = async ({ workspaceId, sessionId, userId }) => {
//   const returnVal = await http('REMOTE_FILE_REMOVE_ALL', {
//     workspaceId,
//     sessionId,
//     userId,
//   })
//   return returnVal
// }
