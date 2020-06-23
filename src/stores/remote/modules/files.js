import Vue from 'vue'
import {
  FILE_RESET,
  SHOW_IMAGE,
  SHOW_AR_IMAGE,
  ADD_HISTORY,
  UPDATE_HISTORY,
  REMOVE_HISTORY,
  ADD_FILE,
  REMOVE_FILE,
  ADD_PDF_PAGE,
  REMOVE_PDF_PAGE,
  SET_CAPTURE,
  CLEAR_CAPTURE,
} from '../mutation-types'

const state = {
  historyList: [],
  fileList: [],
  pdfPages: {},
  selected: null,
  shareFile: {},
  shareArImage: {},
  captureFile: {},
}

const mutations = {
  [FILE_RESET](state) {
    state.historyList = []
    state.fileList = []
    state.pdfPages = {}
    state.selected = null
    state.shareFile = {}
    state.shareArImage = {}
    state.captureFile = {}
  },
  [SHOW_IMAGE](state, imgInfo) {
    state.shareFile = Object.assign({}, imgInfo)
  },
  [SHOW_AR_IMAGE](state, imgInfo) {
    state.shareArImage = Object.assign({}, imgInfo)
  },
  [ADD_HISTORY](state, imgInfo) {
    // const idx = state.historyList.findIndex(history => history.id === file.id)
    // if (idx < 0) {
    // }
    state.historyList.push(imgInfo)
    state.shareFile = Object.assign({}, imgInfo)
  },
  [UPDATE_HISTORY](state, info) {
    const idx = state.historyList.findIndex(history => history.id === info.id)
    if (idx < 0) return
    for (let key in info) {
      if (key === 'id') continue
      state.historyList[idx][key] = info[key]
    }
  },
  [REMOVE_HISTORY](state, fileId) {
    const idx = state.historyList.findIndex(file => file.id === fileId)
    if (idx < 0) return
    state.historyList.splice(idx, 1)
  },
  [ADD_FILE](state, file) {
    state.fileList.push(file)
  },
  [REMOVE_FILE](state, fileId) {
    const idx = state.fileList.findIndex(file => file.id === fileId)
    if (idx < 0) return
    state.fileList.splice(idx, 1)
  },
  [ADD_PDF_PAGE](state, page) {
    if (!(page.id in state.pdfPages)) {
      Vue.set(state.pdfPages, page.id, [])
    }
    state.pdfPages[page.id].push(page)
  },
  [REMOVE_PDF_PAGE](state, key) {
    if (!(key in state.pdfPages)) return
    delete state.pdfPages[key]
  },
  [SET_CAPTURE](state, imgInfo) {
    state.captureFile = Object.assign({}, imgInfo)
  },
  [CLEAR_CAPTURE](state) {
    state.captureFile = {}
  },
}

export default {
  state,
  mutations,
}
