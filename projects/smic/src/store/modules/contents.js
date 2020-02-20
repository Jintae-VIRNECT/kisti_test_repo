import Vue from 'vue'
import API from '@/models/api'

/**
 * 컨텐츠 리스트 API
 * @param {Object} params
 */
async function requestContentsList(params = {}) {
  try {
    const response = await Vue.axios.get(API.CONTENTS_LIST(), {
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
    contentsList: [],
    contentDetail: {
      info: {},
      sceneGroupList: [],
    },
  },
  getters: {
    getContentsList(state) {
      return state.contentsList
    },
    getContentDetail(state) {
      return state.contentDetail
    },
  },
  mutations: {
    CONTENTS_LIST(state, list) {
      state.contentsList = list
    },
    CONTENT_INFO(state, obj) {
      state.contentDetail.info = obj
    },
    SCENE_GROUP_LIST(state, list) {
      state.contentDetail.sceneGroupList = list
    },
    DELETE_CONTENT(state, contentUUID) {
      state.contentsList = state.contentsList.filter(
        content => content.contentUUID !== contentUUID,
      )
    },
  },
  actions: {
    async CONTENTS_LIST(context, params = {}) {
      try {
        const response = await Vue.axios.get(API.CONTENTS_LIST(), {
          params: {
            search: params.search || '',
            filter: params.filter || 'All',
            sort: params.sort || 'createdDate,desc',
            size: params.size || 20,
            page: params.page || 0,
          },
        })
        const { data } = response.data
        context.commit('CONTENTS_LIST', data.contentInfo)
        return response.data
      } catch (e) {
        console.error(e)
      }
    },
    async SCENE_GROUP_LIST(context, contentUUID) {
      try {
        const response = await Vue.axios.get(API.SCENE_GROUP_LIST(), {
          params: { contentUUID },
        })
        const { data } = response.data
        context.commit(
          'CONTENT_INFO',
          context.state.contentsList.find(
            content => content.contentUUID === contentUUID,
          ),
        )
        context.commit('SCENE_GROUP_LIST', data.sceneGroupInfoList)
        return response.data
      } catch (e) {
        console.error(e)
      }
    },
    async DELETE_CONTENT(context, contentUUID) {
      try {
        const response = await Vue.axios.delete(
          API.CONTENT_DETAIL(contentUUID),
          {
            params: {
              uuid: context.getters.getUser.uuid,
            },
          },
        )
        const { code, message } = response.data
        if (code === 200) context.commit('DELETE_CONTENT', contentUUID)
        else throw new Error(message)
        return response.data
      } catch (e) {
        console.error(e)
      }
    },
  },
}
