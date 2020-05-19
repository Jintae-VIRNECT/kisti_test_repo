import http from 'api/gateway'

/**
 * ----------------------------------------------------------------
 * | !!!!!Warning! these functions return mocking response!!!!!!! |
 * ----------------------------------------------------------------
 */

/**
 * 최근 협업 목록 요청
 * @param {Object} param params for http request
 * @param {Number} param.page history page number
 * @param {Boolean} param.paging history paging option
 * @param {Number} param.size history paging size
 */
export const getHistoryList = async function(param) {
  const returnVal = await http('GET_HISTORY_LIST', param)

  return returnVal
}

/**
 * 최근 협업 목록중 단일 항목에 대한 세부 내용 요청
 * @param {Object} param params for http request
 * @param {Number} param.roomId history room id for request
 *
 */
export const getHistorySingleItem = async function(param) {
  const { roomId } = param
  const returnVal = await http('GET_HISTORY_ITEM', { roomId })

  return returnVal
}

/**
 * 최근 협업 목록중 단일 항목 제거
 * @param {Object} param
 * @param {Number} roomId roomId for delete
 */
export const deleteHistorySingleItem = async function(param) {
  const { roomId } = param
  const returnVal = await http('DELETE_HISTORY_ITEM', { roomId })

  return returnVal
}

/**
 * 최근 협업 목록 모두 삭제
 */
export const deleteAllHistory = async function() {
  const returnVal = await http('DELETE_HISTORY_ALL')

  return returnVal
}
