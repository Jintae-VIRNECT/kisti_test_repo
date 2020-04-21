import http from 'api/gateway'

//dummyFile
import dummyJsonHistory from './history.json'
import dummyJsonHistorySingleItem from './historyId.json'

/**
 * ----------------------------------------------------------------
 * | !!!!!Warning! these functions return mocking response!!!!!!! |
 * ----------------------------------------------------------------
 */

/**
 * 최근 협업 목록을 요청
 */
export const getHistoryList = async function() {
  //const returnVal = await http('GET_HISTORY_LIST')

  const returnVal = dummyJsonHistory
  return returnVal
}

/**
 * 최근 협업 목록의 세부 내용 요청
 * @param {*} historyId 상세 조회할 최근 기록의 id
 */
export const getHistorySingleItem = async function(param) {
  let { historyId } = param

  //const returnVal = await http('GET_HISTORY_ITEM', historyId)

  const returnVal = dummyJsonHistorySingleItem
  console.log(returnVal)
  return returnVal
}

/**
 * 최근 협업 목록중 단일 항목 제거
 * @param {Object} param
 */
export const deleteHistorySingleItem = async function(param) {
  const { historyId } = param

  //const returnVal = await http('DELETE_HISTORY_ITEM', historyId)

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
