/**
 * 프로젝트에 관련된 데이터를 관리합니다.
 * projectList: 프로젝트 목록, projectUUIDList: 프로젝트 id 값만 넣는 배열, searchParams 프로젝트 조회시 sort, filter 등의 쿼리값 객체
 * currentIndex: 클릭한 프로젝트의 목록 인덱스 번호, pageMeta: 프로젝트 목록을 통해 받아오는 페이징 관련데이터
 * totalElements: 총 프로젝트 갯수, currentTotalElements: 클릭한 프로젝트까지의 총 프로젝트 갯수
 */
import projectService from '@/services/project'
import SearchParams from '@/models/project/SearchParams'

export default {
  state: () => ({
    projectList: [],
    projectUUIDList: [],
    searchParams: {},
    currentIndex: -1,
    pageMeta: {
      currentPage: 1,
      currentSize: 10,
      totalElements: 0,
      totalPage: 0,
    },
    totalElements: 0,
    currentTotalElements: 0,
  }),
  getters: {
    projectList(state) {
      return state.projectList
    },
    projectUUIDList(state) {
      return state.projectUUIDList
    },
    searchParams(state) {
      return state.searchParams
    },
    currentIndex(state) {
      return state.currentIndex
    },
    pageMeta(state) {
      return state.pageMeta
    },
    totalElements(state) {
      return state.totalElements
    },
    currentTotalElements(state) {
      return state.currentTotalElements
    },
  },
  mutations: {
    SET_PROJECT_LIST(state, list) {
      state.projectList = list
    },
    SET_PROJECT_UUID_LIST(state) {
      state.projectUUIDList = state.projectList.map(project => project.uuid)
    },
    SET_PARAMS(state, params) {
      state.searchParams = params
    },
    SET_CURRENT_INDEX(state, uuid) {
      const isUUIDExist = projectUUID => projectUUID === uuid
      state.currentIndex = state.projectUUIDList.findIndex(isUUIDExist)
    },
    SET_PAGE_META(state, meta) {
      state.pageMeta = meta
    },
    SET_TOTAL_ELEMENTS(state, total) {
      state.totalElements = total
    },
    SET_CURRENT_TOTAL_ELEMENTS(state) {
      const currentPageGreaterOne =
        (state.pageMeta.currentPage - 1) * state.pageMeta.currentSize +
        state.currentIndex
      const currentPageOne = state.currentIndex

      state.currentTotalElements =
        state.pageMeta.currentPage > 1 ? currentPageGreaterOne : currentPageOne
    },
  },
  actions: {
    async getProjectList({ dispatch }, searchParams = {}) {
      const { list, pageMeta } = await projectService.searchProjects(
        Object.assign({}, searchParams),
      )
      dispatch('setProjectState', { list, pageMeta, searchParams })

      return { list, pageMeta }
    },
    setProjectState({ commit }, { list, pageMeta, searchParams = {} }) {
      commit('SET_PROJECT_LIST', list)
      commit('SET_PROJECT_UUID_LIST')
      commit('SET_TOTAL_ELEMENTS', pageMeta.totalElements)
      commit('SET_PAGE_META', pageMeta)
      commit('SET_PARAMS', new SearchParams(searchParams))
    },
    clearAllProjectData({ commit }) {
      commit('SET_CURRENT_INDEX', -1)
      commit('SET_CURRENT_TOTAL_ELEMENTS', 0)
    },
  },
}
