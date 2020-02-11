import Vue from 'vue'

export default {
  state: {
    contentsList: [],
  },
  getters: {
    getContentsList(state) {
      return state.contentsList
    },
  },
  mutations: {
    CONTENTS_LIST(state, { list }) {
      state.contentsList = list
    },
  },
  actions: {
    async CONTENTS_LIST(context, param = {}) {
      try {
        const response = await Vue.axios.get('@content/contents', {
          params: {
            search: param.search || '',
            filter: param.filter || '',
            sort: param.sort || '',
          },
        })
        const { data } = response.data
        context.commit('CONTENTS_LIST', {
          list: data.contentInfo,
        })
        return response.data
      } catch (e) {
        console.error(e)
      }
    },
  },
}
