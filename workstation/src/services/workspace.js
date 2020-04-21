import api from '@/api/gateway'

// model
import Workspace from '@/models/workspace/Workspace'
import Member from '@/models/workspace/Member'

export default {
  /**
   * 워크스페이스 정보
   * @param {String} workspaceId
   */
  async getWorkspaceInfo(workspaceId) {
    const data = await api('WORKSPACE_INFO', {
      route: { workspaceId },
    })
    return {
      info: new Workspace(data.workspaceInfo),
      members: data.workspaceUserInfo.map(user => new Member(user)),
      plansCount: {
        master: data.masterUserCount,
        manager: data.manageUserCount,
        member: data.memberUserCount,
      },
    }
  },
}
