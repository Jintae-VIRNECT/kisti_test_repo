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
  // drawing: line, text
  // ar: pointing, area, drawing

  mic: false,
  speaker: true,
}

const mutations = {
  [CALL_RESET](state) {
    state.view = VIEW.STREAM
    state.action = ACTION.STREAM_DEFAULT
  },
  // ????
  // [CALL_MODE_SET](state, info) {
  //   state.roomInfo = info
  // },
  [CALL_MODE_SET](state, mode) {
    state.view = mode
  },
  [CALL_ACTION_SET](state, mode) {
    state.action = mode
  },
  [TOOL_DRAWING_COLOR](state, mode) {
    state.drawColor = mode
  },
  [TOOL_DRAWING_OPACITY](state, mode) {
    state.drawOpacity = mode
  },
  [TOOL_TEXT_SIZE](state, mode) {
    state.fontSize = mode
  },
  [TOOL_LINE_WIDTH](state, mode) {
    state.lineWidth = mode
  },
  [CALL_MIC](state, mode) {
    state.mic = mode
  },
  [CALL_SPEAKER](state, mode) {
    state.speaker = mode
  },
}

export default {
  state,
  mutations,
}
