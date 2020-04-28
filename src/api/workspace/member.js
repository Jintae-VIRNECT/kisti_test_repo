import http from 'api/gateway'
import dummyJsonMember from './member.json'

/**
 * ----------------------------------------------------------------
 * | !!!!!Warning! these functions return mocking response!!!!!!! |
 * ----------------------------------------------------------------
 */

export const getMemberList = async function() {
  //const returnVal = await http('GET_MEMBER_LIST')
  const returnVal = dummyJsonMember
  return returnVal
}
