import http from 'api/gateway'
import _ from 'lodash'

//dummyFile
import dummyJsonHistory from './history.json'
import dummyJsonHistorySingleItem from './roomdata.json'

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

  const returnVal = _.cloneDeep(dummyJsonHistory)
  return returnVal
}

/**
 * 최근 협업 목록중 단일 항목에 대한 세부 내용 요청
 * @param {Object} param 상세 조회할 최근 기록의 roomid
 */
export const getHistorySingleItem = async function(param) {
  let { roomId } = param

  //const returnVal = await http('GET_HISTORY_ITEM', roomId)
  const returnVal = _.cloneDeep(dummyJsonHistorySingleItem)

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
