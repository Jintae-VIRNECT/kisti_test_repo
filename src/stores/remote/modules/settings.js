//Definition of workspace store
import { SETTINGS } from '../mutation-types'

const state = {
  audioInputDevice: null,
  audioOutputDevice: null,
  videoDevice: null,
  videoQuality: null,
  language: null,
  recordLength: null,
  localRecordLength: null,
  recordResolution: null,
  notiFlagPc: false,
}

const mutations = {
  [SETTINGS.SET_AUDIO_INPUT_DEVICE](state, audioInputDevice) {
    state.audioInputDevice = audioInputDevice
  },
  [SETTINGS.SET_AUDIO_OUTPUT_DEVICE](state, audioOutputDevice) {
    state.audioOutputDevice = audioOutputDevice
  },
  [SETTINGS.SET_VIDEO_DEVICE](state, videoDevice) {
    state.videoDevice = videoDevice
  },
  [SETTINGS.SET_VIDEO_QUALITY](state, videoQuality) {
    state.videoQuality = videoQuality
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
  [SETTINGS.SET_NOTI_FLAG_PC](state, notiFlagPc) {
    state.notiFlagPc = notiFlagPc
  },
}
export default {
  state,
  mutations,
}
