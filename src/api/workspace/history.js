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
 *
 * @param {Object} header header for http request
 * @param {String} header.userId uuid for user
 * @param {String} header.workspaceId uuid for workspaceId
 */
export const getHistoryList = async function(param, header) {
  //for test data
  //remove after devleop!!
  header = {
    userId: '498b1839dc29ed7bb2ee90ad6985c608',
    workspaceId: '4d6eab0860969a50acbfa4599fbb5ae8',
  }

  const returnVal = await http('GET_HISTORY_LIST', param, header)

  return returnVal
}

/**
 * 최근 협업 목록중 단일 항목에 대한 세부 내용 요청
 * @param {Object} param 상세 조회할 최근 기록의 roomid
 *
 * @param {Object} header header for http request
 * @param {String} header.userId uuid for user
 * @param {String} header.workspaceId uuid for workspaceId
 */
export const getHistorySingleItem = async function(param, header) {
  //for test data
  //remove after devleop!!
  const roomId = 18
  header = {
    userId: '498b1839dc29ed7bb2ee90ad6985c608',
    workspaceId: '4d6eab0860969a50acbfa4599fbb5ae8',
  }

  //const { roomId } = param
  const returnVal = await http('GET_HISTORY_ITEM', { roomId }, header)

  return returnVal
}

/**
 * 최근 협업 목록중 단일 항목 제거
 * @param {Object} param
 */
export const deleteHistorySingleItem = async function(param) {
  const { roomId } = param

  //const returnVal = await http('DELETE_HISTORY_ITEM', roomId)

  const returnVal = {
    code: 200,
    message: 'complete',
  }
  return returnVal
}

/**
 * 최근 협업 목록 모두 삭제
 */
export const deleteAllHistory = async function() {
  //const returnVal = await http('DELETE_HISTORY_ALL')

  const returnVal = {
    code: 200,
    message: 'complete',
  }
  return returnVal
}
