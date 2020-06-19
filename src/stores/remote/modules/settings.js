//Definition of workspace store
import { SETTINGS } from '../mutation-types'

const state = {
  micDevice: null,
  speakerDevice: null,
  videoDevice: null,
  language: null,
  localRecordLength: null,
  recordResolution: '480p',
  localRecordInterval: '60',
  allowPointing: false,
  allowLocalRecording: false,

  //may need move other store
  //stream for local steram(screen stream)
  screenStream: null,

  localRecordTarget: 'recordWorker',
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
  [SETTINGS.SET_LOCAL_RECORD_INTERVAL](state, localRecordInterval) {
    state.localRecordInterval = localRecordInterval
  },

  [SETTINGS.SET_ALLOW_POINTING](state, allowPointing) {
    state.allowPointing = allowPointing
  },

  [SETTINGS.SET_ALLOW_LOCAL_RECORDING](state, allowLocalRecording) {
    state.allowLocalRecording = allowLocalRecording
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
