import { api } from '@/plugins/axios'
import { store } from '@/plugins/context'
import Workspace from '@/models/workspace/Workspace'
import Plan from '@/models/workspace/Plan'
import profileServices from '@/services/profile'

function getMyWorkspaces() {
  return store.getters['auth/myWorkspaces']
}
function getMyProfile() {
  return store.getters['auth/myProfile']
}

export default {
  getMyWorkspaces,
  /**
   * 마스터 워크스페이스 정보 가져오기
   */
  async getMasterWorkspaceInfo() {
    const masterWorkspace = getMyWorkspaces().find(
      workspace => workspace.role === 'MASTER',
    )
    const { workspaceInfo } = await api('GET_WORKSPACE_INFO', {
      route: { workspaceId: masterWorkspace.uuid },
    })
    const masterWorkspaceInfo = new Workspace(workspaceInfo)
    const { nickname, image } = getMyProfile()
    masterWorkspaceInfo.masterNickName = nickname
    masterWorkspaceInfo.masterProfile = image
    return masterWorkspaceInfo
  },
  /**
   * 워크스페이스 목록 검색
   * @param {Object} searchParams
   */
  async searchWorkspaces(searchParams) {
    const { workspaceList, pageMeta } = await api('GET_WORKSPACES', {
      params: {
        userId: profileServices.getMyProfile().uuid,
        size: 6,
        ...searchParams,
      },
    })
    return {
      list: workspaceList.map(workspace => new Workspace(workspace)),
      total: pageMeta.totalElements,
    }
  },
  /**
   * 사용중인 플랜 검색
   * @param {Object} searchParams
   */
  async searchUsingPlans(searchParams) {
    const data = [0, 1, 2, 3, 4]
    return {
      list: data.map(plan => new Plan(plan)),
      total: data.length,
    }
  },
}
