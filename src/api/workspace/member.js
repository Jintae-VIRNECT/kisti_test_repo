import http from 'api/gateway'
// import dummyJsonMember from './member.json'

/**
 * ----------------------------------------------------------------
 * | !!!!!Warning! these functions return mocking response!!!!!!! |
 * ----------------------------------------------------------------
 */

export const getMemberList = async function({ workspaceId }) {
  const returnVal = await http('GET_MEMBER_LIST', { workspaceId })
  // const returnVal = dummyJsonMember
  return returnVal
}
