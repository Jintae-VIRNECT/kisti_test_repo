// PF-workspace 서버 접근
// Guest 멤버 로그인용
import http from 'api/gateway'

/**
 * Guest 정보 및 오픈 룸 정보를 반환
 *
 * @query {String} workspaceId
 * @query {String} userId
 * @returns {Object} 룸 정보
 */
export const getWorkspaceInfo = async function({ workspaceId }) {
  const returnVal = await http('WORKSPACE_INFO', { workspaceId })
  return returnVal
}
