import api from '@/api/gateway'

// model
import Workspace from '@/models/workspace/Workspace'
import Member from '@/models/workspace/Member'

function activeWorkspaceGetter() {
  return $nuxt.$store.getters['workspace/activeWorkspace']
}
function myProfileGetter() {
  return $nuxt.$store.getters['auth/myProfile']
}

export default {
  /**
   * 활성 워크스페이스 변경 감시
   * @param {function} func
   */
  watchActiveWorkspace(func) {
    $nuxt.$store.watch(activeWorkspaceGetter, func)
  },
  /**
   * 내가 속한 워크스페이스 리스트 -> { 마스터, 매니저, 멤버 }
   */
  async getMyWorkspaces() {
    const data = await api('WORKSPACES_LIST', {
      params: {
        userId: myProfileGetter().uuid,
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
   * 신규 참여 멤버
   * @param {function} searchParams
   */
  async getNewMembers() {
    const data = await api('WORKSPACE_NEW_MEMBERS', {
      route: {
        workspaceId: activeWorkspaceGetter().info.uuid,
      },
    })
    return data.map(member => new Member(member))
  },
  /**
   * 멤버 리스트 검색
   * @param {function} searchParams
   */
  async searchMembers(searchParams) {
    const { memberInfoList, pageMeta } = await api('MEMBER_LIST', {
      route: {
        workspaceId: activeWorkspaceGetter().info.uuid,
      },
      params: {
        userId: myProfileGetter().uuid,
        filter: 'MASTER,MANAGER,MEMBER',
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
   * @param {form} form
   */
  async startWorkspace(form) {
    const options = { params: form }
    if (form.profile) {
      const formData = new FormData()
      Object.entries(form).forEach(([key, val]) => {
        formData.append(key, val)
      })
      options.params = formData
      options.params.headers = {
        'Content-Type': 'multipart/form-data',
      }
    }
    await api('WORKSPACE_START', options)
  },
  /**
   * 워크스페이스 프로필 설정 변경
   * @param {form} form
   */
  async updateWorkspaceInfo(form) {
    const options = { params: form }
    if (form.profile) {
      const formData = new FormData()
      Object.entries(form).forEach(([key, val]) => {
        formData.append(key, val)
      })
      options.params = formData
      options.params.headers = {
        'Content-Type': 'multipart/form-data',
      }
    }
    const data = await api('WORKSPACE_EDIT', options)
    // 변경된 내용 적용
    $nuxt.$store.dispatch('workspace/getActiveWorkspaceInfo', {
      route: {
        workspaceId: data.uuid,
      },
    })
  },
  /**
   * 멤버 권한 변경
   * @param {form} form
   */
  async updateMembersRole(form) {
    const data = await api('MEMBER_ROLE_UPDATE', {
      route: {
        workspaceId: activeWorkspaceGetter().info.uuid,
      },
      params: {
        ...form,
        masterUserId: myProfileGetter().uuid,
      },
    })
  },
  /**
   * 멤버 추방
   * @param {string} uuid
   */
  async kickMember(uuid) {
    const workspaceId = activeWorkspaceGetter().info.uuid
    const data = await api('MEMBER_KICK', {
      route: { workspaceId },
      params: {
        kickedUserId: uuid,
        userId: myProfileGetter().uuid,
        workspaceId,
      },
    })
  },
  /**
   * 멤버 초대
   * @param {[InviteMember]} userInfoList
   */
  async inviteMembers(userInfoList) {
    const workspaceId = activeWorkspaceGetter().info.uuid
    const data = await api('MEMBERS_INVITE', {
      route: { workspaceId },
      params: {
        userId: myProfileGetter().uuid,
        userInfoList,
      },
    })
  },
}
