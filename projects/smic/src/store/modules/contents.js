import Vue from 'vue'
import API from '@/models/api'

export default {
  state: {
    contentsList: [],
    contentDetail: {
      info: {},
      sceneGroupList: [],
    },
  },
  getters: {
    contentsList(state) {
      return state.contentsList
    },
    contentDetail(state) {
      return state.contentDetail
    },
  },
  mutations: {
    SET_CONTENTS_LIST(state, list) {
      state.contentsList = list
    },
    SET_CONTENT_INFO(state, obj) {
      state.contentDetail.info = obj
    },
    SET_SCENE_GROUP_LIST(state, list) {
      state.contentDetail.sceneGroupList = list
    },
    DELETE_CONTENT(state, contentUUID) {
      state.contentsList = state.contentsList.filter(
        content => content.contentUUID !== contentUUID,
      )
    },
  },
  actions: {
    // 컨텐츠 리스트
    async getContentsList(context, params = {}) {
      const response = await Vue.axios.get(API.CONTENTS_LIST(), {
        params: {
          search: params.search || '',
          filter: params.filter || 'All',
          sort: params.sort || 'createdDate,desc',
          size: params.size || 20,
          page: params.page || 0,
        },
      })
      const { code, data, message } = response.data
      if (code === 200) {
        context.commit('SET_CONTENTS_LIST', data.contentInfo)
        return data
      } else throw new Error(message)
    },
    // 세부공정 컨텐츠 리스트
    async getSceneGroupList(context, contentUUID) {
      const response = await Vue.axios.get(API.SCENE_GROUP_LIST(), {
        params: { contentUUID },
      })
      const { code, data, message } = response.data
      if (code === 200) {
        context.commit(
          'SET_CONTENT_INFO',
          context.state.contentsList.find(
            content => content.contentUUID === contentUUID,
          ),
        )
        context.commit('SET_SCENE_GROUP_LIST', data.sceneGroupInfoList)
        return data
      } else throw new Error(message)
    },
    // 컨텐츠 삭제
    async deleteContent(context, contentUUID) {
      const response = await Vue.axios.delete(API.CONTENT_DETAIL(contentUUID), {
        params: {
          uuid: context.getters.getUser.uuid,
        },
      })
      const { code, data, message } = response.data
      if (code === 200) {
        context.commit('DELETE_CONTENT', contentUUID)
        return data
      } else throw new Error(message)
    },
  },
}
