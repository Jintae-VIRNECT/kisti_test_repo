import {
  CALL_MODE_SET,
  CALL_ACTION_SET,
  CALL_SPEAKER,
  CALL_MIC,
  MUTE_ON_OFF
} from '../mutation-types'

const state = {
  view: 'stream', // stream, sharing, ar
  action: 'pointing', // pointing, drawing
  mic: true,
  speaker: true,
  unmute: true
}

const mutations = {
  [CALL_MODE_SET](state, mode) {
    state.view = mode;
  },
  [CALL_ACTION_SET](state, mode) {
    state.action = mode;
  },
  [CALL_SPEAKER](state) {
    state.speaker = !state.speaker
  },
  [CALL_MIC](state) {
    state.mic = !state.mic
  },
  [MUTE_ON_OFF](state) {
    state.unmute = !state.unmute
  }
}

export default {
    state,
    mutations
}