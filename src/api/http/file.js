import http from 'api/gateway'

//서버 녹화파일 / 로컬 녹화 파일 / 첨부파일 API 전부

/**
 * 세션내의 서버 녹화 파일을 불러오는 API
 * @query {String} workspaceId 워크스페이스 id
 * @query {String} userId 유저 id
 * @param {String} sessionId 세션 id
 */
export const getServerRecordFiles = async ({
  workspaceId,
  userId,
  sessionId,
}) => {
  const returnVal = await http('SERVER_RECORD_FILES', {
    workspaceId,
    userId,
    sessionId,
  })
  return returnVal
}

/**
 * 특정 서버 녹화 파일을 삭제하는 API
 * @param {*} param0
 */
export const deleteServerRecordFileItem = async ({
  workspaceId,
  userId,
  id,
}) => {
  const returnVal = await http('DELETE_SERVER_RECORD_FILES_ITEM', {
    workspaceId,
    userId,
    id,
  })
  return returnVal
}

/**
 * 특정 서버 녹화파일을 다운로드 하기위한 URL을 반환하는 API
 * @query {String} workspaceId 워크스페이스 id
 * @query {String} userId 유저 id
 * @query {String} id 레코딩 id
 */
export const getServerRecordFileUrl = async ({ workspaceId, userId, id }) => {
  const returnVal = await http('SERVER_RECORD_FILE_URL', {
    workspaceId,
    userId,
    id,
  })
  return returnVal
}

/**
 * 세션내의 첨부 파일 목록을 반환하는 API
 *
 * @query {String}  workspaceId 워크스페이스 id
 * @query {String}  sessionId 세션 id
 * @param {String}  userId 유저 id
 * @param {Boolean} deleted 삭제된 파일 필터
 * @param {Number}  page 페이지 번호
 * @param {Boolean} paging 페이징 유무
 * @param {Number}  size 페이징 사이즈
 */
export const getAttachFiles = async ({
  workspaceId,
  userId,
  sessionId,
  opts = { deleted: false, page: 0, size: 100, paging: false },
}) => {
  const returnVal = await http('FILES', {
    workspaceId,
    userId,
    sessionId,
    ...opts,
  })
  return returnVal
}

/**
 * 세션에 업로드된 로컬녹화 파일 목록을 반환
 *
 * @query {String} workspaceId
 * @query {String} sessionId
 * @param {Number} page 페이지 번호
 * @param {Boolean} deleted 삭제 파일 필터
 * @param {Number} size 페이징 사이즈
 * @param {String} sort 정렬옵션
 * @param {String} userId 유저id
 * @param {Boolean} paging 페이징 옵션
 *
 */
export const getLocalRecordFiles = async ({
  deleted = false,
  page,
  sessionId,
  size,
  sort,
  userId,
  workspaceId,
  paging = false,
}) => {
  const returnVal = await http('LOCAL_RECORD_FILES', {
    deleted,
    page,
    sessionId,
    size,
    sort,
    userId,
    workspaceId,
    paging,
  })
  return returnVal
}

/**
 * 특정 로컬 녹화 파일을 삭제하는 API
 * @param {*} param0
 */
export const deleteLocalRecordFileItem = async ({
  objectName,
  sessionId,
  userId,
  workspaceId,
}) => {
  const returnVal = await http('DELETE_RECORD_FILE_ITEM', {
    objectName,
    sessionId,
    userId,
    workspaceId,
  })
  return returnVal
}

/**
 * 로컬 녹화파일을 다운로드 하기위한 URL을 반환하는 API
 *
 * @query {String} workspaceId 워크스페이스 id
 * @query {String} sessionId 세션 id
 * @param {String} objectName minio 고유 파일명
 * @param {String} userId 유저 id
 */
export const getLocalRecordFileUrl = async ({
  objectName,
  sessionId,
  userId,
  workspaceId,
}) => {
  const returnVal = await http('LOCAL_RECORD_FILE_DOWNLOAD_URL', {
    objectName,
    sessionId,
    userId,
    workspaceId,
  })
  return returnVal
}

/**
 * 특정 파일을 삭제하는 API
 * @param {*} param0
 */
export const deleteFileItem = async ({
  objectName,
  sessionId,
  userId,
  workspaceId,
}) => {
  const returnVal = await http('DELETE_FILE_ITEM', {
    objectName,
    sessionId,
    userId,
    workspaceId,
  })
  return returnVal
}

/**
 * 특정 파일의 다운로드 URL을 반환하는 API
 * @param {*} param0
 */
export const getFileDownloadUrl = async ({
  objectName,
  sessionId,
  userId,
  workspaceId,
}) => {
  console.log({
    objectName,
    sessionId,
    userId,
    workspaceId,
  })
  const returnVal = await http('FILE_ITEM_DOWNLOAD_URL', {
    objectName,
    sessionId,
    userId,
    workspaceId,
  })
  return returnVal
}
