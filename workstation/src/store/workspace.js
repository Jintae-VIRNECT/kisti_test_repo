import api from '@/api/gateway'
import Workspace from '@/models/workspace/Workspace'
import Cookies from 'js-cookie'

export default {
  state: () => ({
    myWorkspaces: [],
    activeWorkspace: {},
  }),
  getters: {
    myWorkspaces(state) {
      return state.myWorkspaces
    },
    activeWorkspace(state) {
      return state.activeWorkspace
    },
  },
  mutations: {
    SET_MY_WORKSPACES(state, arr) {
      state.myWorkspaces = arr
    },
    SET_ACTIVE_WORKSPACE(state, workspaceId) {
      state.activeWorkspace = state.myWorkspaces.find(
        workspace => workspace.uuid === workspaceId,
      )
      Cookies.set('activeWorkspace', workspaceId, { expires: 7 })
    },
  },
  actions: {
    async getMyWorkspaces({ commit }, userId) {
      const data = await api('WORKSPACES_LIST', {
        params: { userId },
      })
      commit(
        'SET_MY_WORKSPACES',
        data.workspaceList.map(workspace => new Workspace(workspace)),
      )
    },
  },
}
