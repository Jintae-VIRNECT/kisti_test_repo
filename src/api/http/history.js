import http from 'api/gateway'

/**
 * 최근 협업 목록 요청
 * @param {Number} page size 대로 나눠진 페이지를 조회할 번호(1부터 시작)
 * @param {Boolean} paging 검색 결과 페이지네이션 여부
 * @param {Number} size 페이징 사이즈
 * @param {String} sort 정렬 옵션 데이터
 * @param {String} userId 필수값
 * @param {String} workspaceId 필수값
 *
 */
export const getHistoryList = async function({
  page = 0,
  paging = false,
  size = 15,
  sort = 'createdDate,desc',
  userId,
  workspaceId,
}) {
  const returnVal = await http('HISTORY_LIST', {
    page,
    paging,
    size,
    sort,
    userId,
    workspaceId,
  })

  return returnVal
}

/**
 * 최근 협업 목록 검색
 * @param {Number} page size 대로 나눠진 페이지를 조회할 번호(1부터 시작)
 * @param {String} search 검색할 텍스트
 * @param {Number} size 페이징 사이즈
 * @param {String} sort 정렬 옵션 데이터
 * @param {String} userId 필수값
 * @param {String} workspaceId 필수값
 *
 */
export const searchHistoryList = async function({
  page = 0,
  search = '',
  size = 15,
  sort = 'createdDate,desc',
  userId,
  workspaceId,
}) {
  const returnVal = await http('HISTORY_SEARCH', {
    page,
    search,
    size,
    sort,
    userId,
    workspaceId,
  })

  return returnVal
}

/**
 * 최근 협업 목록중 단일 항목에 대한 세부 내용 요청
 * @param {String} workspaceId 워크스페이스 id
 * @param {String} sessionId 세션 id
 */
export const getHistorySingleItem = async function({ workspaceId, sessionId }) {
  const returnVal = await http('HISTORY_ITEM', { workspaceId, sessionId })

  return returnVal
}

/**
 * 최근 협업 목록중 단일 항목 제거
 * @param {String} workspaceId 워크스페이스 id
 * @param {Array[String]} sessionIdList 삭제할 히스토리 세션 Id
 * @param {String} uuid 유저 id
 */
export const deleteHistorySingleItem = async function({
  workspaceId,
  sessionIdList = [],
  uuid,
}) {
  const returnVal = await http('DELETE_HISTORY_ITEM', {
    workspaceId,
    sessionIdList,
    uuid,
  })

  return returnVal
}
/**
 * 최근 협업 목록 모두 삭제
 * @param {String} workspaceId 워크스페이스 id
 * @param {String} userId 유저 id
 */
export const deleteAllHistory = async function({ workspaceId, userId }) {
  const returnVal = await http('DELETE_HISTORY_ALL', { workspaceId, userId })

  return returnVal
}
