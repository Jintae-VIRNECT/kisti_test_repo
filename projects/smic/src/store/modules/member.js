import Vue from 'vue'
import API from '@/models/api'

export default {
  state: {
    memberList: [],
  },
  getters: {
    memberList(state) {
      return state.memberList
    },
  },
  mutations: {
    SET_MEMBER_LIST(state, list) {
      state.memberList = list
    },
  },
  actions: {
    async getMemberList(state, param = {}) {
      const response = await Vue.axios.get(API.MEMBER_LIST(), {
        params: {
          userId: this.getters.getUser.uuid,
          search: param.search || '',
          filter: param.filter || 'ALL',
          sort: param.sort || 'name,asc',
        },
      })
      const { code, data, message } = response.data
      if (code === 200) {
        state.commit('SET_MEMBER_LIST', data.memberInfoList)
        return data
      } else throw new Error(message)
    },
  },
}
