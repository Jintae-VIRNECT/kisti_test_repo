import { api } from '@/plugins/axios'
import { store } from '@/plugins/context'

// model
import WorkspaceInfo from '@/models/workspace/WorkspaceInfo'
import Member from '@/models/workspace/Member'
import MemberActivity from '@/models/workspace/MemberActivity'
import PlansInfo from '@/models/workspace/PlansInfo'
import OnPremiseSetting from '@/models/workspace/OnPremiseSetting'

function activeWorkspaceGetter() {
  return store.getters['auth/activeWorkspace']
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
  /**
   * 워크스페이스 정보
   * @param {string} workspaceId
   * @returns {WorkspaceInfo} 워크스페이스 모델
   */
  async getWorkspaceInfo(workspaceId) {
    const data = await api('WORKSPACE_INFO', {
      route: { workspaceId },
    })
    return new WorkspaceInfo(data)
  },
  /**
   * 신규 참여 멤버
   * @param {object} searchParams
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
   * @param {object} searchParams
   */
  async searchMembers(searchParams = {}) {
    const { memberInfoList, pageMeta } = await api('MEMBER_LIST', {
      route: {
        workspaceId: activeWorkspaceGetter().uuid,
      },
      params: {
        sort: 'role,asc',
        size: 10,
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
      .filter(([, val]) => val)
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
   * 워크스페이스 전용 계정 아이디 중복 체크
   * @param {string} userId
   */
  async checkMembersId(userId) {
    const data = await api('MEMBER_ID_CHECK', {
      params: {
        email: userId,
      },
    })

    return data.result ? true : false
  },
  /**
   * 워크스페이스 프로필 설정 변경
   * @param {form} form
   */
  async updateWorkspaceInfo(form) {
    const options = { params: form }
    const formData = new FormData()
    Object.entries(form)
      .filter(([, val]) => val)
      .forEach(([key, val]) => {
        formData.append(key, val)
      })
    options.params = formData
    options.params.headers = {
      'Content-Type': 'multipart/form-data',
    }
    const data = await api('WORKSPACE_EDIT', options)
    // 변경된 내용 적용
    await store.dispatch('auth/getAuthInfo')
    store.commit('auth/SET_ACTIVE_WORKSPACE', data.uuid)
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

    await api('WORKSPACE_LEAVE', {
      route: { workspaceId: activeWorkspaceGetter().uuid },
      params: formData,
    })
  },
  /**
   * 멤버 프로필 사진 업데이트
   * @param {object} form 폼 객체
   */
  async updateMembersProfile(form) {
    if (form.profile) {
      const formData = new FormData()
      formData.append('profile', form.profile)
      formData.append('requestUserId', myProfileGetter().uuid)
      formData.append('userId', form.userId)
      await api('MEMBER_PROFILE_UPDATE', {
        route: {
          workspaceId: activeWorkspaceGetter().uuid,
        },
        params: formData,
        headers: {
          'Content-Type': 'multipart/form-data',
        },
      })
    }
  },
  /**
   * 멤버 정보 변경 (플랜, 닉네임, 권한)
   * @param {form} form
   */
  async updateMembersInfo(form) {
    await api('MEMBER_INFO_UPDATE', {
      route: {
        workspaceId: activeWorkspaceGetter().uuid,
      },
      params: {
        ...form,
        requestUserId: myProfileGetter().uuid,
      },
    })
    store.dispatch('plan/getPlansInfo')
  },
  /**
   * 멤버 추방
   * @param {string} uuid
   */
  async kickMember(uuid) {
    const formData = new FormData()
    formData.append('kickedUserId', uuid)
    formData.append('userId', myProfileGetter().uuid)

    await api('MEMBER_KICK', {
      route: { workspaceId: activeWorkspaceGetter().uuid },
      params: formData,
    })
    store.dispatch('plan/getPlansInfo')
  },
  /**
   * 멤버 초대
   * @param {[InviteMember]} userInfoList
   */
  async inviteMembers(userInfoList) {
    await api('MEMBERS_INVITE', {
      route: { workspaceId: activeWorkspaceGetter().uuid },
      params: {
        userId: myProfileGetter().uuid,
        userInfoList,
      },
    })
    store.dispatch('plan/getPlansInfo')
  },
  /**
   * 게스트 생성
   * @param {object} form
   */
  async createGuest(form) {
    await api('MEMBER_GUEST_CREATE', {
      route: { workspaceId: activeWorkspaceGetter().uuid },
      params: {
        ...form,
        userId: myProfileGetter().uuid,
      },
    })
    store.dispatch('plan/getPlansInfo')
  },
  /**
   * 멤버 활동 조회
   * @param {object} searchParams
   */
  async searchMembersActivity(searchParams) {
    const data = await api('MEMBERS_ACTIVITY', {
      route: {
        workspaceUUID: activeWorkspaceGetter().uuid,
      },
      params: {
        size: 10,
        ...searchParams,
      },
    })
    return {
      list: data.infos.map(activity => new MemberActivity(activity)),
      total: data.pageMeta.totalElements,
    }
  },
  /**
   * 워크스페이스 플랜 정보 조회
   */
  async getWorkspacePlansInfo() {
    const data = await api('GET_WORKSPACE_PLAN_INFO', {
      route: { workspaceId: activeWorkspaceGetter().uuid },
    })
    return new PlansInfo(data)
  },
  /**
   * 멤버 생성 (onpremise)
   * @param {[CreateMember]} userInfoList
   */
  async createMembers(userInfoList) {
    userInfoList.forEach(user => {
      delete user.duplicateCheck
    })

    const data = await api('MEMBERS_CREATE', {
      route: { workspaceId: activeWorkspaceGetter().uuid },
      params: {
        userId: myProfileGetter().uuid,
        memberAccountCreateRequest: userInfoList,
      },
    })
    store.dispatch('plan/getPlansInfo')
    return data
  },
  /**
   * 게스트 멤버 삭제
   * @param {string} uuid
   * @param {string} password
   */
  async deleteGuestMember(uuid, password) {
    const params = {
      requestUserId: myProfileGetter().uuid,
      userId: uuid,
    }

    if (password !== undefined) {
      params.requestUserPassword = password
    }

    const data = await api('MEMBER_GUEST_DELETE', {
      route: { workspaceId: activeWorkspaceGetter().uuid },
      params: {
        ...params,
      },
    })
    store.dispatch('plan/getPlansInfo')
    return data
  },
  /**
   * 멤버 삭제 (onpremise)
   * @param {string} uuid
   * @param {string} password
   */
  async deleteMember(uuid, password) {
    const params = {
      requestUserId: myProfileGetter().uuid,
      userId: uuid,
    }

    if (password !== undefined) {
      params.requestUserPassword = password
    }

    const data = await api('MEMBER_DELETE', {
      route: { workspaceId: activeWorkspaceGetter().uuid },
      params: {
        ...params,
      },
    })
    store.dispatch('plan/getPlansInfo')
    return data
  },
  /**
   * 멤버 비밀번호 변경 (onpremise)
   * @param {string} uuid
   * @param {string} password
   */
  async changeMembersPassword(uuid, password) {
    const data = await api('MEMBER_CHANGE_PASSWORD', {
      route: { workspaceId: activeWorkspaceGetter().uuid },
      params: {
        requestUserId: myProfileGetter().uuid,
        userId: uuid,
        password: password,
      },
    })
    return data
  },
  /**
   * 워크스페이스 세팅 불러오기 (onpremise)
   */
  async getWorkspaceSetting() {
    const data = await api('WORKSPACE_GET_SETTING')
    return new OnPremiseSetting(data)
  },
  /**
   * 워크스페이스 타이틀 설정 (onpremise)
   * @param {string} title
   */
  async setWorkspaceTitle(title) {
    const workspaceId = activeWorkspaceGetter().uuid
    const data = await api('WORKSPACE_SET_TITLE', {
      route: { workspaceId },
      params: {
        title,
        workspaceId,
        userId: myProfileGetter().uuid,
      },
    })
    return data
  },
  /**
   * 워크스페이스 로고 설정 (onpremise)
   * @param {File} logo
   * @param {File} whiteLogo
   */
  async setWorkspaceLogo(logo, whiteLogo) {
    const formData = new FormData()
    formData.append('userId', myProfileGetter().uuid)
    if (logo) formData.append('defaultLogo', logo)
    if (whiteLogo) {
      formData.append('whiteLogo', whiteLogo)
      formData.append('greyLogo', whiteLogo)
    }

    const data = await api('WORKSPACE_SET_LOGO', {
      route: { workspaceId: activeWorkspaceGetter().uuid },
      params: formData,
    })
    return data
  },
  /**
   * 워크스페이스 파비콘 설정 (onpremise)
   */
  async setWorkspaceFavicon(favicon) {
    const formData = new FormData()
    formData.append('userId', myProfileGetter().uuid)
    if (favicon) formData.append('favicon', favicon)

    const data = await api('WORKSPACE_SET_FAVICON', {
      route: { workspaceId: activeWorkspaceGetter().uuid },
      params: formData,
    })
    return data
  },
}
