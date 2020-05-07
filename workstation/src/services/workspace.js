import api from '@/api/gateway'

// model
import Workspace from '@/models/workspace/Workspace'
import Member from '@/models/workspace/Member'

function myWorkspacesGetter() {
  return $nuxt.$store.getters['workspace/myWorkspaces']
}
function activeWorkspaceGetter() {
  return $nuxt.$store.getters['workspace/activeWorkspace']
}
function myProfileGetter() {
  return $nuxt.$store.getters['auth/myProfile']
}

export default {
  /**
   * 활성 워크스페이스 변경 감시
   * @param {component} that
   * @param {function} func
   */
  watchActiveWorkspace(that, func) {
    const watch = $nuxt.$store.watch(activeWorkspaceGetter, func)
    that.$on('hook:beforeDestroy', watch)
  },
  /**
   * 내가 속한 워크스페이스 리스트
   */
  getMyWorkspaces() {
    return myWorkspacesGetter()
  },
  async getWorkspaceInfo(workspaceId) {
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
  /**
   * 신규 참여 멤버
   * @param {function} searchParams
   */
  async getNewMembers() {
    const data = await api('WORKSPACE_NEW_MEMBERS', {
      route: {
        workspaceId: activeWorkspaceGetter().uuid,
      },
    })
    return data.map(member => new Member(member))
  },
  /**
   * 멤버 리스트 검색
   * @param {function} searchParams
   */
  async searchMembers(searchParams = {}) {
    const { memberInfoList, pageMeta } = await api('MEMBER_LIST', {
      route: {
        workspaceId: activeWorkspaceGetter().uuid,
      },
      params: {
        sort: 'role,asc',
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
    const formData = new FormData()
    Object.entries(form)
      .filter(([key, val]) => val)
      .forEach(([key, val]) => {
        formData.append(key, val)
      })
    options.params = formData
    options.params.headers = {
      'Content-Type': 'multipart/form-data',
    }
    await api('WORKSPACE_START', options)
  },
  /**
   * 워크스페이스 프로필 설정 변경
   * @param {form} form
   */
  async updateWorkspaceInfo(form) {
    const options = { params: form }
    const formData = new FormData()
    Object.entries(form)
      .filter(([key, val]) => val)
      .forEach(([key, val]) => {
        formData.append(key, val)
      })
    options.params = formData
    options.params.headers = {
      'Content-Type': 'multipart/form-data',
    }
    const data = await api('WORKSPACE_EDIT', options)
    // 변경된 내용 적용
    await $nuxt.$store.dispatch(
      'workspace/getMyWorkspaces',
      myProfileGetter().uuid,
    )
    $nuxt.$store.commit('workspace/SET_ACTIVE_WORKSPACE', data.uuid)
  },
  /**
   * 멤버 권한 변경
   * @param {form} form
   */
  async updateMembersRole(form) {
    const data = await api('MEMBER_ROLE_UPDATE', {
      route: {
        workspaceId: activeWorkspaceGetter().uuid,
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
    const formData = new FormData()
    formData.append('kickedUserId', uuid)
    formData.append('userId', myProfileGetter().uuid)

    const data = await api('MEMBER_KICK', {
      route: { workspaceId: activeWorkspaceGetter().uuid },
      params: formData,
    })
  },
  /**
   * 멤버 초대
   * @param {[InviteMember]} userInfoList
   */
  async inviteMembers(userInfoList) {
    const data = await api('MEMBERS_INVITE', {
      route: { workspaceId: activeWorkspaceGetter().uuid },
      params: {
        userId: myProfileGetter().uuid,
        userInfoList,
      },
    })
  },
}
