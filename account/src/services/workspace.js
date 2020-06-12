import { api } from '@/plugins/axios'
import Workspace from '@/models/workspace/Workspace'
import profileServices from '@/services/profile'

function getMyWorkspaces() {
  return process.client && [...$nuxt.$store.getters['auth/myWorkspaces']]
}

export default {
  getMyWorkspaces,
  async getWorkspaceList(searchParams) {
    const { workspaceList, pageMeta } = await api('GET_WORKSPACES', {
      params: {
        userId: profileServices.getMyProfile().uuid,
        ...searchParams,
      },
    })
    return {
      list: workspaceList.map(workspace => new Workspace(workspace)),
      // total: pageMeta.totalElements,
    }
  },
}
