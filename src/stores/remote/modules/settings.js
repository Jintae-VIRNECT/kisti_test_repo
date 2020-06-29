//Definition of workspace store
import { SETTINGS } from '../mutation-types'

const state = {
  mic: {
    deviceId: null,
    isOn: false,
  },
  speaker: {
    deviceId: null,
    isOn: true,
  },
  localRecordInfo: {
    time: null,
    interval: '60',
    resolution: '480p',
  },
  allow: {
    pointing: false,
    localRecording: false,
  },

  videoDevice: null,
  language: null,

  //may need move other store
  //stream for local steram(screen stream)
  screenStream: null,

  localRecordTarget: 'recordWorker',
}

const mutations = {
  [SETTINGS.SET_MIC_DEVICE](state, mic) {
    Object.assign(state.mic, mic)
  },
  [SETTINGS.SET_SPEAKER_DEVICE](state, speaker) {
    Object.assign(state.speaker, speaker)
  },
  [SETTINGS.SET_RECORD](state, recordInfo) {
    Object.assign(state.localRecordInfo, recordInfo)
  },
  [SETTINGS.SET_ALLOW](state, allow) {
    Object.assign(state.allow, allow)
  },

  [SETTINGS.SET_VIDEO_DEVICE](state, videoDevice) {
    state.videoDevice = videoDevice
  },
  [SETTINGS.SET_LANGUAGE](state, language) {
    state.language = language
  },

  [SETTINGS.SET_SCREEN_STREAM](state, screenStream) {
    state.screenStream = screenStream
  },

  [SETTINGS.SET_LOCAL_RECORD_TARGET](state, localRecordTarget) {
    state.localRecordTarget = localRecordTarget
  },
}
export default {
  state,
  mutations,
}
