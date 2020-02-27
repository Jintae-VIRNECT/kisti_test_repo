import api from '@/api/gateway'

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
      state.contentDetail = { ...state.contentDetail, info: obj }
    },
    SET_SCENE_GROUP_LIST(state, list) {
      state.contentDetail = { ...state.contentDetail, sceneGroupList: list }
    },
    DELETE_CONTENT(state, contentUUID) {
      state.contentsList = state.contentsList.filter(
        content => content.contentUUID !== contentUUID,
      )
    },
  },
  actions: {
    // 컨텐츠 리스트
    async getContentsList(context, param) {
      const data = await api('CONTENTS_LIST', {
        params: {
          sort: 'createdDate,desc',
          size: 20,
          ...param,
        },
      })
      context.commit('SET_CONTENTS_LIST', data.contentInfo)
      return data
    },
    // 세부공정 컨텐츠 리스트
    async getSceneGroupList(context, contentUUID) {
      const data = await api('SCENE_GROUP_LIST', {
        params: { contentUUID },
      })
      context.commit('SET_SCENE_GROUP_LIST', data.sceneGroupInfoList)
      context.commit(
        'SET_CONTENT_INFO',
        context.state.contentsList.find(
          content => content.contentUUID === contentUUID,
        ),
      )
      return data
    },
    // 컨텐츠 상세조회
    async getContentsDetail(context, contentUUID) {
      const data = await api('CONTENT_INFO', {
        query: { contentUUID },
      })
      context.commit('SET_CONTENT_INFO', data)
      return data
    },
    // 컨텐츠 삭제
    async deleteContent(context, contentUUID) {
      const data = await api('CONTENT_DELETE', {
        query: { contentUUID },
        params: { uuid: context.getters.getUser.uuid },
      })
      context.commit('SET_CONTENT_INFO', data)
      context.commit('DELETE_CONTENT', contentUUID)
      return data
    },
  },
}
