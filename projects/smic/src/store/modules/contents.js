import Vue from 'vue'

export default {
  state: {
    conentsList: [],
  },
  getters: {
    getConentsList(state) {
      return state.conentsList
    },
  },
  mutations: {
    CONTENTS_LIST(state, { list }) {
      state.conentsList = list
    },
  },
  actions: {
    async CONTENTS_LIST(context, param = {}) {
      try {
        const response = await Vue.axios.get('@content/conents', {
          params: {
            search: param.search || '',
            filter: param.filter || '',
            sort: param.sort || '',
          },
        })
        const { data } = response.data
        context.commit('CONTENTS_LIST', {
          list: data.conentsList,
        })
        return response.data
      } catch (e) {
        console.error(e)
      }
    },
  },
}
