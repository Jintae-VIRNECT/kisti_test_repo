import Vue from 'vue'

export default {
  state: {
    memberList: [],
  },
  getters: {
    getMemberList(state) {
      return state.memberList
    },
  },
  mutations: {
    MEMBER_LIST(state, { list }) {
      state.memberList = list
    },
  },
  actions: {
    async MEMBER_LIST(context, param = {}) {
      try {
        const response = await Vue.axios.get('/users', {
          params: {
            uuid: this.getters.getUser.uuid,
            search: param.search || '',
            sort: param.sort || 'name,asc',
          },
        })
        const { data } = response.data
        context.commit('MEMBER_LIST', {
          list: data.userInfoList,
        })
        return response.data
      } catch (e) {
        console.error(e)
      }
    },
  },
}
