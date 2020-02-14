import Vue from 'vue'
import API from '@/models/api'

/**
 * 컨텐츠 리스트 API
 * @param {Object} params
 */
async function requestContentsList(params = {}) {
  try {
    const response = await Vue.axios.get(API.GET_CONTENTS_LIST(), {
      params: {
        search: params.search || '',
        filter: params.filter || 'All',
        sort: params.sort || 'createdDate,desc',
        size: params.size || 20,
        page: params.page || 0,
      },
    })
    return response.data
  } catch (e) {
    console.error(e)
  }
}

export default {
  state: {
    currentContentsList: [],
    contentsList: [],
  },
  getters: {
    getCurrentContentsList(state) {
      return state.currentContentsList
    },
    getContentsList(state) {
      return state.contentsList
    },
  },
  mutations: {
    CURRENT_CONTENTS_LIST(state, list) {
      state.currentContentsList = list
    },
    CONTENTS_LIST(state, list) {
      state.contentsList = list
    },
  },
  actions: {
    async CURRENT_CONTENTS_LIST(context) {
      const res = await requestContentsList()
      context.commit('CURRENT_CONTENTS_LIST', res.data.contentInfo)
      return res
    },
    async CONTENTS_LIST(context, params = {}) {
      const res = await requestContentsList(params)
      context.commit('CONTENTS_LIST', res.data.contentInfo)
      return res
    },
  },
}
