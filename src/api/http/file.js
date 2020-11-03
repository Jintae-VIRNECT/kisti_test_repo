/*eslint no-unused-vars: "error"*/

import http from 'api/gateway'

//서버 녹화파일 / 로컬 녹화 파일 / 첨부파일 API 전부

/**
 * 모든 서버 녹화 파일을 불러오는 API
 * @param {*} workspaceId
 * @param {*} userId
 * @param {*} sessionId
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
 * 모든 서버 녹화 파일을 삭제하는 API
 * @param {*} param0
 */
export const deleteServerRecordFilesAll = async ({ workspaceId, userId }) => {
  const returnVal = await http('DELETE_SERVER_RECORD_FILES_ALL', {
    workspaceId,
    userId,
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
 * @param {*} param0
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
 * 모든 파일 목록을 반환하는 API
 * @param {*} param0
 */
export const getAttachFiles = async ({
  workspaceId,
  userId,
  sessionId,
  opts = {},
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
 * 
 * @param {*} param0 
 */
export const getLocalRecordFiles = async({
  deleted,
  page,
  sessionId,
  size,
  sort,
  userId,
  workspaceId,
}) => {
  const returnVal = await http('LOCAL_RECORD_FILES', {
    deleted,
    page,
    sessionId,
    size,
    sort,
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
  workspaceId,
  userId,
  sessionId,
}) => {
  const returnVal = await http('DELETE_FILE_ITEM', {
    objectName,
    workspaceId,
    userId,
    sessionId,
  })
  return returnVal
}

/**
 * 특정 파일의 다운로드 URL을 반환하는 API
 * @param {*} param0
 */
export const getFileDownloadUrl = async ({ workspaceId, sessionId }) => {
  const returnVal = await http('FILE_ITEM_DOWNLOAD_URL', {
    workspaceId,
    sessionId,
  })
  return returnVal
}
