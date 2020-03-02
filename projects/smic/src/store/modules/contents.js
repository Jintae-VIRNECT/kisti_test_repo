import api from '@/api/gateway'

export default {
  state: {
    contentsList: [],
    contentsTotal: 0,
    contentDetail: {
      info: {},
      sceneGroupList: [],
    },
  },
  getters: {
    contentsList(state) {
      return state.contentsList
    },
    contentsTotal(state) {
      return state.contentsTotal
    },
    contentDetail(state) {
      return state.contentDetail
    },
  },
  mutations: {
    SET_CONTENTS_LIST(state, list) {
      state.contentsList = list
    },
    SET_CONTENTS_TOTAL(state, num) {
      state.contentsTotal = num
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
          ...param,
        },
      })
      context.commit('SET_CONTENTS_LIST', data.contentInfo)
      context.commit('SET_CONTENTS_TOTAL', data.pageMeta.totalElements)
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
        route: { contentUUID },
      })
      context.commit('SET_CONTENT_INFO', data)
      return data
    },
    // 컨텐츠 삭제
    async deleteContent(context, contentUUID) {
      const data = await api('CONTENT_DELETE', {
        route: { contentUUID },
        params: { uuid: context.getters.getUser.uuid },
      })
      context.commit('SET_CONTENT_INFO', data)
      context.commit('DELETE_CONTENT', contentUUID)
      return data
    },
  },
}
