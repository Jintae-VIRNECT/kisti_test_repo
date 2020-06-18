import { api } from '@/plugins/axios'
import { store } from '@/plugins/context'
import Workspace from '@/models/workspace/Workspace'
import Plan from '@/models/workspace/Plan'
import profileServices from '@/services/profile'

function getMyWorkspaces() {
  return store.getters['auth/myWorkspaces']
}

export default {
  getMyWorkspaces,
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
  async searchUsingPlans(searchParams) {
    const data = [0, 1, 2, 3, 4]
    return {
      list: data.map(plan => new Plan(plan)),
      total: data.length,
    }
  },
}
