import { api } from '@/plugins/axios'
import { store } from '@/plugins/context'

// model
import WorkspaceInfo from '@/models/workspace/WorkspaceInfo'
import Member from '@/models/workspace/Member'

function activeWorkspaceGetter() {
  return store.getters['workspace/activeWorkspace']
}
function myProfileGetter() {
  return store.getters['auth/myProfile']
}

export default {
  /**
   * 활성 워크스페이스 변경 감시
   * @param {component} that
   * @param {function} func
   */
  watchActiveWorkspace(that, func) {
    const watch = store.watch(activeWorkspaceGetter, func)
    that.$on('hook:beforeDestroy', watch)
  },
  async getWorkspaceInfo(workspaceId) {
    const data = await api('WORKSPACE_INFO', {
      route: { workspaceId },
    })
    return new WorkspaceInfo(data)
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
   * 멤버 전체 리스트
   */
  async allMembers() {
    const { memberInfoList } = await api('MEMBER_LIST_ALL', {
      route: {
        workspaceId: activeWorkspaceGetter().uuid,
      },
    })
    return memberInfoList.map(member => new Member(member))
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
   * 멤버 정보 상세 조회
   * @param {string} userId
   */
  async getMemberInfo(userId) {
    const data = await api('MEMBER_INFO', {
      route: {
        workspaceId: activeWorkspaceGetter().uuid,
      },
      params: { userId },
    })
    return new Member(data)
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
    await store.dispatch('workspace/getMyWorkspaces', myProfileGetter().uuid)
    store.commit('workspace/SET_ACTIVE_WORKSPACE', data.uuid)
  },
  /**
   * 워크스페이스 나가기
   * @param {string} uuid
   */
  async workspaceLeave(uuid) {
    const formData = new FormData()
    formData.append('kickedUserId', uuid)
    formData.append('userId', myProfileGetter().uuid)
    formData.append('workspaceId ', activeWorkspaceGetter().uuid)

    const data = await api('WORKSPACE_LEAVE', {
      route: { workspaceId: activeWorkspaceGetter().uuid },
      params: formData,
    })
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
        requestUserId: myProfileGetter().uuid,
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
