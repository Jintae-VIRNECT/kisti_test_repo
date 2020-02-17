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
    sceneGroupList: [],
  },
  getters: {
    getCurrentContentsList(state) {
      return state.currentContentsList
    },
    getContentsList(state) {
      return state.contentsList
    },
    getSceneGroupList(state) {
      return state.sceneGroupList
    },
  },
  mutations: {
    CURRENT_CONTENTS_LIST(state, list) {
      state.currentContentsList = list
    },
    CONTENTS_LIST(state, list) {
      state.contentsList = list
    },
    SCENE_GROUP_LIST(state, list) {
      state.sceneGroupList = list
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
    async SCENE_GROUP_LIST(context, param = {}) {
      try {
        const response = await Vue.axios.get(API.GET_SCENE_GROUP_LIST(), {
          params: {
            contentUUID: param.contentUUID,
          },
        })
        const { data } = response.data
        context.commit('SCENE_GROUP_LIST', data.sceneGroupInfoList)
        return response.data
      } catch (e) {
        console.error(e)
      }
    },
  },
}
