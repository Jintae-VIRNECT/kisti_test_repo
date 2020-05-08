import {
  CALL_MODE_SET,
  CALL_ACTION_SET,
  CALL_STREAM,
  CALL_SPEAKER,
  CALL_MIC,
  MUTE_ON_OFF,
  TOOL_DRAWING_COLOR,
  TOOL_DRAWING_OPACITY,
  TOOL_TEXT_SIZE,
  TOOL_LINE_WIDTH,
} from '../mutation-types'

import { reset } from 'utils/toolOption'

const state = {
  view: 'stream', // stream, sharing, ar
  drawColor: reset.color,
  drawOpacity: reset.opacity,
  lineWidth: reset.width,
  fontSize: reset.size,
  action: 'pointing', // pointing, drawing
  stream: true,
  mic: true,
  speaker: true,
  unmute: true,
}

const mutations = {
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
  [CALL_STREAM](state, payload) {
    state.stream = payload
  },
  [CALL_MIC](state, payload) {
    state.mic = payload
  },
  [CALL_SPEAKER](state, payload) {
    state.speaker = payload
  },
  [MUTE_ON_OFF](state) {
    state.unmute = !state.unmute
  },
}

export default {
  state,
  mutations,
}
