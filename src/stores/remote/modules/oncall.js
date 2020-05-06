import {
  CALL_MODE_SET,
  CALL_TOOL_SET,
  CALL_ACTION_SET,
  CALL_STREAM,
  CALL_SPEAKER,
  CALL_MIC,
  MUTE_ON_OFF,
} from '../mutation-types'

const state = {
  view: 'stream', // stream, sharing, ar
  tool: 'pointing',
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
  [CALL_TOOL_SET](state, mode) {
    state.tool = mode
  },
  [CALL_ACTION_SET](state, mode) {
    state.action = mode
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
