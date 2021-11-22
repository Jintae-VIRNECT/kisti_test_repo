import {
  CALL_RESET,
  CALL_MODE_SET,
  CALL_ACTION_SET,
  TOOL_DRAWING_COLOR,
  TOOL_DRAWING_OPACITY,
  TOOL_TEXT_SIZE,
  TOOL_LINE_WIDTH,
  CALL_MIC,
  CALL_SPEAKER,
  SET_TAB_MENU_NOTICE,
  SET_ALLOW_CAMERA_CONTROL,
  SET_IS_3D_POSITION_PICKING,
  SET_AR_3D_SHARE_STATUS,
} from '../mutation-types'

import { reset } from 'utils/callOptions'
import { VIEW, ACTION } from 'configs/view.config'

const state = {
  view: VIEW.STREAM, // stream, drawing, ar
  drawColor: reset.color,
  drawOpacity: reset.opacity,
  lineWidth: reset.width,
  fontSize: reset.size,
  action: ACTION.STREAM_DEFAULT,
  // stream: default, pointing
  // drawing: line, text, lock
  // ar: pointing, area, drawing, 3d

  allowCameraControlFlag: false,

  mic: false,
  speaker: true,
  is3dPositionPicking: false,
  ar3dShareStatus: '',

  settingModal: false,
  recordModal: false,
  usingStt: false,
  tabMenus: [
    {
      text: 'service.stream',
      key: VIEW.STREAM,
      icon: require('assets/image/call/gnb_ic_shareframe.svg'),
      notice: false,
    },
    {
      text: 'service.drawing',
      key: VIEW.DRAWING,
      icon: require('assets/image/call/gnb_ic_creat_basic.svg'),
      notice: false,
    },
    {
      text: 'service.ar',
      key: VIEW.AR,
      icon: require('assets/image/call/gnb_ic_creat_ar.svg'),
      notice: false,
    },
  ],
}

const mutations = {
  ['SETTING_MODAL'](state, flag) {
    state.settingModal = flag
  },
  ['RECORD_MODAL'](state, flag) {
    state.recordModal = flag
  },
  ['USE_STT'](state, flag) {
    state.usingStt = flag
  },

  [CALL_RESET](state) {
    state.view = VIEW.STREAM
    state.action = ACTION.STREAM_DEFAULT
    state.settingModal = false
    state.recordModal = false
  },
  // ????
  // [CALL_MODE_SET](state, info) {
  //   state.roomInfo = info
  // },
  [CALL_MODE_SET](state, mode) {
    state.view = mode
  },
  [CALL_ACTION_SET](state, action) {
    state.action = action
  },
  [TOOL_DRAWING_COLOR](state, color) {
    state.drawColor = color
  },
  [TOOL_DRAWING_OPACITY](state, opacity) {
    state.drawOpacity = opacity
  },
  [TOOL_TEXT_SIZE](state, size) {
    state.fontSize = size
  },
  [TOOL_LINE_WIDTH](state, width) {
    state.lineWidth = width
  },
  [CALL_MIC](state, status) {
    state.mic = status
  },
  [CALL_SPEAKER](state, status) {
    state.speaker = status
  },
  [SET_TAB_MENU_NOTICE](state, { index, notice }) {
    state.tabMenus[index].notice = notice
  },
  [SET_ALLOW_CAMERA_CONTROL](state, flag) {
    state.allowCameraControlFlag = flag
  },
  [SET_IS_3D_POSITION_PICKING](state, flag) {
    state.is3dPositionPicking = flag
  },
  [SET_AR_3D_SHARE_STATUS](state, status) {
    state.ar3dShareStatus = status
  },
}
const actions = {
  showModalSetting({ commit }, flag) {
    commit('SETTING_MODAL', flag)
  },
  showModalRecord({ commit }, flag) {
    commit('RECORD_MODAL', flag)
  },
  useStt({ commit }, flag) {
    commit('USE_STT', flag)
  },
}
const getters = {
  modalSetting: state => state.settingModal,
  modalRecord: state => state.recordModal,
  usingStt: state => state.usingStt,
  tabMenus: state => state.tabMenus,
  allowCameraControlFlag: state => state.allowCameraControlFlag,
  is3dPositionPicking: state => state.is3dPositionPicking,
  ar3dShareStatus: state => state.ar3dShareStatus,
}

export default {
  state,
  mutations,
  actions,
  getters,
}
