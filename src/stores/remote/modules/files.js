import Vue from 'vue'
import { ACTION } from 'configs/view.config'
import {
  FILE_RESET,
  SHOW_IMAGE,
  SHOW_AR_IMAGE,
  UPDATE_SHARE,
  ADD_HISTORY,
  UPDATE_HISTORY,
  REMOVE_HISTORY,
  ADD_FILE,
  REMOVE_FILE,
  ADD_PDF_PAGE,
  REMOVE_PDF_PAGE,
  SET_CAPTURE,
  CALL_ACTION_SET,
  SHOW_3D_CONTENT,
} from '../mutation-types'

const state = {
  historyList: [],
  fileList: [],
  pdfPages: {},
  selected: null,
  shareFile: {},
  shareArImage: {},
  captureFile: {},
  share3dContent: {},
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
    state.shareFile.json = []
  },
  [UPDATE_SHARE](state, info) {
    if (!state.shareFile || !state.shareFile.id) return
    for (let key in info) {
      state.shareFile[key] = info[key]
    }
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
  [SHOW_3D_CONTENT](state, content) {
    state.share3dContent = Object.assign({}, content)
  },
}

const actions = {
  /**
   * show image
   * @param {Object} fileInfo
   */
  showImage({ commit }, fileInfo) {
    commit(SHOW_IMAGE, fileInfo)
  },
  /**
   * show image
   * @param {Object} fileInfo
   */
  showArImage({ commit }, fileInfo) {
    commit(SHOW_AR_IMAGE, fileInfo)
    if (fileInfo && fileInfo.id) {
      commit(CALL_ACTION_SET, ACTION.AR_DRAWING)
    }
  },
  /**
   * add file history
   * @param {Object} fileInfo
   */
  addHistory({ commit }, fileInfo) {
    commit(ADD_HISTORY, fileInfo)
  },
  /**
   * add file history
   * @param {Object} fileInfo
   */
  updateHistory({ commit }, fileInfo) {
    if ('json' in fileInfo) {
      commit(UPDATE_SHARE, {
        json: fileInfo['json'],
      })
      delete fileInfo['json']
    }
    commit(UPDATE_HISTORY, fileInfo)
  },
  /**
   * remove file history
   * @param {String} fileId
   */
  removeHistory({ commit }, fileId) {
    commit(REMOVE_HISTORY, fileId)
  },
  /**
   * add file
   * @param {Object} fileInfo
   */
  addFile({ commit }, fileInfo) {
    commit(ADD_FILE, fileInfo)
  },
  /**
   * add file history
   * @param {String} fileId
   */
  removeFile({ commit }, fileId) {
    commit(REMOVE_PDF_PAGE, fileId)
    commit(REMOVE_FILE, fileId)
  },
  /**
   * add pdf pages
   * @param {Object} pdfImageInfo
   */
  addPdfPage({ commit }, pdfImageInfo) {
    commit(ADD_PDF_PAGE, pdfImageInfo)
  },
  /**
   * remove pdf pages
   * @param {Object} pdfId
   */
  removePdfPage({ commit }, pdfId) {
    commit(REMOVE_PDF_PAGE, pdfId)
  },

  /**
   * set capture image
   * @param {Object} fileInfo
   */
  setCapture({ commit }, fileInfo) {
    commit(SET_CAPTURE, fileInfo)
  },
  /**
   * remove capture image
   */
  clearCapture({ commit }) {
    commit(SET_CAPTURE, {})
  },
}

const getters = {
  historyList: state => state.historyList,
  fileList: state => state.fileList,
  pdfPages: state => state.pdfPages,
  shareFile: state => state.shareFile,
  shareArImage: state => state.shareArImage,
  captureFile: state => state.captureFile,
  share3dContent: state => state.share3dContent,
}

export default {
  state,
  mutations,
  actions,
  getters,
}
