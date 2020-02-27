import api from '@/api/gateway'

export default {
  state: {
    memberList: [],
    memberTotal: 0,
  },
  getters: {
    memberList(state) {
      return state.memberList
    },
    memberTotal(state) {
      return state.memberTotal
    },
  },
  mutations: {
    SET_MEMBER_LIST(state, list) {
      state.memberList = list
    },
    SET_MEMBER_TOTAL(state, count) {
      state.memberTotal = count
    },
  },
  actions: {
    async getMemberList(state, param) {
      const data = await api('MEMBER_LIST', {
        params: {
          userId: this.getters.getUser.uuid,
          size: 10,
          ...param,
        },
      })
      state.commit('SET_MEMBER_LIST', data.memberInfoList)
      state.commit('SET_MEMBER_TOTAL', data.pageMeta.totalElements)
      return data
    },
  },
}
