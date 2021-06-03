import http from 'api/gateway'

/**
 * 협업보드 파일 업로드
 * @param {File} file
 * @param {String} sessionId
 * @param {String} userId
 * @param {String} workspaceId
 */
export const drawingUpload = async ({
  file,
  sessionId,
  userId,
  workspaceId,
}) => {
  const returnVal = await http('DRAWING_UPLOAD', {
    file,
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
 */
export const drawingList = async ({
  deleted = false,
  page = 0,
  size = 10,
  paging = false,
  sort = 'createdDate,DESC',
  sessionId,
  workspaceId,
}) => {
  const returnVal = await http('DRAWING_LIST', {
    deleted,
    page,
    paging,
    size,
    sort,
    sessionId,
    workspaceId,
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
export const drawingDownload = async ({
  sessionId,
  workspaceId,
  objectName,
  userId,
}) => {
  const returnVal = await http('DRAWING_DOWNLOAD', {
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
export const drawingRemove = async ({
  workspaceId,
  sessionId,
  leaderUserId,
  objectName,
}) => {
  const returnVal = await http('DRAWING_REMOVE', {
    workspaceId,
    sessionId,
    leaderUserId,
    objectName,
  })
  return returnVal
}

/**
 * 협업보드 공유 파일 전체 삭제
 * @path {String} workspaceId
 * @path {String} sessionId
 * @query {String} userId
 */
export const drawingRemoveAll = async ({ workspaceId, sessionId, userId }) => {
  const returnVal = await http('DRAWING_REMOVE_ALL', {
    workspaceId,
    sessionId,
    userId,
  })
  return returnVal
}
