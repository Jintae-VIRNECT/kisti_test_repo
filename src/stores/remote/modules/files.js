import {
  ADD_HISTORY,
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
}

const mutations = {
  [ADD_HISTORY](state, file) {
    state.historyList.push(file)
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
