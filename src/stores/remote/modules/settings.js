//Definition of workspace store
import { SETTINGS } from '../mutation-types'

const state = {
  micDevice: null,
  speakerDevice: null,
  videoDevice: null,
  language: null,
  localRecordLength: null,
  recordResolution: null,
  recordInterval: null,
}

const mutations = {
  [SETTINGS.SET_MIC_DEVICE](state, mic) {
    state.micDevice = mic
  },
  [SETTINGS.SET_SPEAKER_DEVICE](state, speaker) {
    state.speakerDevice = speaker
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
  [SETTINGS.SET_LOCAL_RECORD_INTERVAL](state, recordInterval) {
    state.recordResolution = recordInterval
  },
}
export default {
  state,
  mutations,
}
