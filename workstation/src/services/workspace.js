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
   * 멤버 리스트 검색
   * @param {function} searchParams
   */
  async searchMembers(searchParams) {
    const { memberInfoList, pageMeta } = await api('MEMBER_LIST', {
      route: {
        workspaceId:
          $nuxt.$store.getters['workspace/activeWorkspace'].info.uuid,
      },
      params: {
        userId: $nuxt.$store.getters['auth/myProfile'].uuid,
        ...searchParams,
      },
    })
    return {
      list: memberInfoList.map(member => new Member(member)),
      total: pageMeta.totalElements,
    }
  },
  /**
   * 워크스페이스 시작
   * @param {FormData} formData
   */
  async startWorkspace(formData) {
    await api('WORKSPACE_START', {
      headers: {
        'Content-Type': 'multipart/form-data',
      },
      params: formData,
    })
  },
  /**
   * 워크스페이스 프로필 설정 변경
   * @param {FormData} formData
   */
  async updateWorkspaceInfo(formData) {
    const data = await api('WORKSPACE_EDIT', {
      headers: {
        'Content-Type': 'multipart/form-data',
      },
      params: formData,
    })
    // 변경된 내용 적용
    $nuxt.$store.dispatch('workspace/getActiveWorkspaceInfo', {
      route: {
        workspaceId: data.uuid,
      },
    })
  },
}
