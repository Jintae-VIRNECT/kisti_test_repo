import api from '@/api/gateway'

// model
import Workspace from '@/models/workspace/Workspace'
import Member from '@/models/workspace/Member'

export default {
  /**
   * 활성 워크스페이스 변경 감시
   * @param {function} func
   */
  watchActiveWorkspace(func) {
    $nuxt.$store.watch(
      () => $nuxt.$store.getters['workspace/activeWorkspace'],
      func,
    )
  },
  /**
   * 내가 속한 워크스페이스 리스트 -> { 마스터, 매니저, 멤버 }
   */
  async getMyWorkspaces() {
    const data = await api('WORKSPACES_LIST', {
      params: {
        userId: $nuxt.$store.getters['auth/myProfile'].uuid,
      },
    })
    const workspaces = data.workspaceList.map(
      workspace => new Workspace(workspace),
    )
    return {
      master: workspaces.filter(workspace => workspace.role === 'MASTER'),
      manager: workspaces.filter(workspace => workspace.role === 'MANAGER'),
      member: workspaces.filter(workspace => workspace.role === 'MEMBER'),
    }
  },
  /**
   * 워크스페이스 정보
   * @param {String} workspaceId
   */
  async getWorkspaceInfo(workspaceId) {
    workspaceId = workspaceId
      ? workspaceId
      : $nuxt.$store.getters['workspace/activeWorkspace']
    const data = await api('WORKSPACE_INFO', {
      route: { workspaceId },
    })
    const members = data.workspaceUserInfo.map(user => new Member(user))
    return {
      info: new Workspace(data.workspaceInfo),
      master: members.find(member => member.role === 'MASTER'),
      managers: members.filter(member => member.role === 'MANAGER'),
      members: members.filter(member => member.role === 'MEMBER'),
    }
  },
}
