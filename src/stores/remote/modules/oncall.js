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
}

export default {
  state,
  mutations,
}
