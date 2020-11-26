import http from 'api/gateway'

/**
 * 유저 최근 협업 목록 요청
 *
 * @param {Number} page 페이지 번호
 * @param {Boolean} paging 페이징 유무
 * @param {Number} size 페이징 크기
 * @param {String} sort 정렬 옵션(ex activeDate,asc)
 * @param {String} userId 유저 id
 * @query {String} workspaceId 워크스페이스 id
 * @param {String} searchWord 검색할 협업명/멤버명(nickName)
 * @param {String} fromTo 기간(ex YYYY-MM-DD,YYYY-MM-DD)
 * @param {String} status 협업 상태
 *
 */
export const getHistoryList = async function({
  page = 0,
  paging = false,
  size = 7,
  sort = 'activeDate,desc',
  userId,
  workspaceId,
  searchWord,
  fromTo,
  status = 'all',
}) {
  const returnVal = await http('HISTORY_LIST', {
    page,
    paging,
    size,
    sort,
    userId,
    workspaceId,
    searchWord,
    fromTo,
    status,
  })

  return returnVal
}

/**
 * 해당워크스페이스의 모든 협업 목록을 반환
 *
 * @param {Number} page 페이지 번호
 * @param {Boolean} paging 페이징 유무
 * @param {Number} size 페이징 크기
 * @param {String} sort 정렬 옵션(ex activeDate,asc)
 * @query {String} workspaceId 워크스페이스 id
 * @param {String} searchWord 검색할 협업명/멤버명(nickName)
 * @param {String} fromTo 기간(ex YYYY-MM-DD,YYYY-MM-DD)
 * @param {String} status 협업 상태
 */
export const getAllHistoryList = async function({
  page = 0,
  paging = false,
  size = 7,
  sort = 'activeDate,desc',
  workspaceId,
  searchWord,
  fromTo,
  status = 'all',
}) {
  const returnVal = await http('ALL_HISTORY_LIST', {
    page,
    paging,
    size,
    sort,
    workspaceId,
    searchWord,
    fromTo,
    status,
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
 * 최근 협업 목록 중 진행중인 세션에 대한 내용 요청
 * @param {String} workspaceId 워크스페이스 id
 * @param {String} sessionId 세션 id
 */
export const getRoomInfo = async ({ workspaceId, sessionId }) => {
  const returnVal = await http('ROOM_INFO', { workspaceId, sessionId })
  return returnVal
}
