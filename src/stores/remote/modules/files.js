import {
  SHOW_IMAGE,
  ADD_HISTORY,
  UPDATE_HISTORY,
  REMOVE_HISTORY,
  ADD_FILE,
  REMOVE_FILE,
  ADD_PDF_PAGE,
  REMOVE_PDF_PAGE,
} from '../mutation-types'

const state = {
  historyList: [],
  fileList: [],
  pdfPages: {},
  selected: null,
  shareFile: {},
}

const mutations = {
  [SHOW_IMAGE](state, imgInfo) {
    state.shareFile = Object.assign({}, imgInfo)
  },
  [ADD_HISTORY](state, imgInfo) {
    // const idx = state.historyList.findIndex(history => history.id === file.id)
    // if (idx < 0) {
    // }
    state.historyList.push(imgInfo)
    state.shareFile = Object.assign({}, imgInfo)
    console.log(imgInfo)
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
      state.pdfPages[page.id] = []
    }
    state.pdfPages[page.id].push(page)
  },
  [REMOVE_PDF_PAGE](state, key) {
    if (!(key in state.pdfPages)) return
    delete state.pdfPages[key]
  },
}

export default {
  state,
  mutations,
}
