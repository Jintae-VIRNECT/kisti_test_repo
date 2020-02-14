import Vue from 'vue'
import API from '@/models/api'

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
        const response = await Vue.axios.get(API.GET_MEMBER_LIST(), {
          params: {
            userId: this.getters.getUser.uuid,
            search: param.search || '',
            filter: param.filter || '',
            sort: param.sort || 'name,asc',
          },
        })
        const { data } = response.data
        context.commit('MEMBER_LIST', {
          list: data.memberList,
        })
        return response.data
      } catch (e) {
        console.error(e)
      }
    },
  },
}
