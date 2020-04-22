export default {
  state: () => ({
    activeWorkspace: null,
  }),
  getters: {
    activeWorkspace(state) {
      return state.activeWorkspace
    },
  },
  mutations: {
    SET_ACTIVE_WORKSPACE(state, uuid) {
      state.activeWorkspace = uuid
    },
  },
}
