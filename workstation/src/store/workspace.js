import api from '@/api/gateway'
import Workspace from '@/models/workspace/Workspace'
import Member from '@/models/workspace/Member'

export default {
  state: () => ({
    activeWorkspace: {},
  }),
  getters: {
    activeWorkspace(state) {
      return state.activeWorkspace
    },
  },
  mutations: {
    SET_ACTIVE_WORKSPACE(state, obj) {
      state.activeWorkspace = obj
    },
  },
  actions: {
    async getActiveWorkspaceInfo({ commit }, params) {
      const data = await api('WORKSPACE_INFO', params)
      const members = data.workspaceUserInfo.map(user => new Member(user))
      commit('SET_ACTIVE_WORKSPACE', {
        info: new Workspace(data.workspaceInfo),
        master: members.find(member => member.role === 'MASTER'),
        managers: members.filter(member => member.role === 'MANAGER'),
        members: members.filter(member => member.role === 'MEMBER'),
      })
    },
  },
}
