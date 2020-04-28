//Definition of workspace store
import { SETTINGS } from '../mutation-types'

const state = {
  mic: null,
  speaker: null,
  videoDevice: null,
  language: null,
  localRecordLength: null,
  recordResolution: null,
}

const mutations = {
  [SETTINGS.SET_MIC](state, mic) {
    state.mic = mic
  },
  [SETTINGS.SET_SPEAKER](state, speaker) {
    state.speaker = speaker
  },
  [SETTINGS.SET_VIDEO_DEVICE](state, videoDevice) {
    state.videoDevice = videoDevice
  },
  [SETTINGS.SET_LANGUAGE](state, language) {
    state.language = language
  },
  [SETTINGS.SET_LOCAL_RECORD_LENGTH](state, localRecordLength) {
    state.localRecordLength = localRecordLength
  },
  [SETTINGS.SET_RECORD_RESOLUTION](state, recordResolution) {
    state.recordResolution = recordResolution
  },
}
export default {
  state,
  mutations,
}
