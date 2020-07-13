import http from 'api/gateway'

export const getMemberList = async function({ workspaceId }) {
  const returnVal = await http('GET_MEMBER_LIST', { workspaceId, size: 100 })
  return returnVal
}
