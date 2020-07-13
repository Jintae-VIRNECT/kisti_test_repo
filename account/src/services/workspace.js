import { api } from '@/plugins/axios'
import { store } from '@/plugins/context'
import Workspace from '@/models/workspace/Workspace'
import Plan from '@/models/workspace/Plan'
import profileServices from '@/services/profile'

function getMyWorkspaces() {
  return store.getters['auth/myWorkspaces']
}
/**
 * 마스터 워크스페이스 정보 가져오기
 */
function getMasterWorkspaceInfo() {
  const masterWorkspace = getMyWorkspaces().find(
    workspace => workspace.role === 'MASTER',
  )
  return masterWorkspace || {}
}

export default {
  getMyWorkspaces,
  getMasterWorkspaceInfo,
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
    const { myPlanInfoList, pageMeta } = await api('GET_MY_PLAN_LIST', {
      route: {
        userId: profileServices.getMyProfile().uuid,
      },
      params: {
        size: 6,
        ...searchParams,
      },
    })
    return {
      list: myPlanInfoList.map(plan => new Plan(plan)),
      total: pageMeta.totalElements,
    }
  },
}
