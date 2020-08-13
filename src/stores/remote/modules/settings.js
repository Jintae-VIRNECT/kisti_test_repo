//Definition of workspace store
import { SETTINGS, TOGGLE_CHAT } from '../mutation-types'
import { RECORD_TARGET, LCOAL_RECORD_STAUTS } from 'utils/recordOptions'

const state = {
  mic: {
    deviceId: null,
    isOn: false,
  },
  speaker: {
    deviceId: null,
    isOn: true,
  },
  video: {
    deviceId: null,
    isOn: true,
  },
  localRecordInfo: {
    time: '60',
    interval: '1',
    resolution: '480p',
  },
  allow: {
    pointing: true,
    localRecording: true,
  },

  videoDevice: null,
  language: null,

  //stream for local steram(screen stream)
  screenStream: null,

  localRecordTarget: RECORD_TARGET.WORKER,
  localRecordStatus: LCOAL_RECORD_STAUTS.STOP,

  chatBox: false,
}

const mutations = {
  [SETTINGS.SET_MIC_DEVICE](state, mic) {
    Object.assign(state.mic, mic)
  },
  [SETTINGS.SET_SPEAKER_DEVICE](state, speaker) {
    Object.assign(state.speaker, speaker)
  },
  [SETTINGS.SET_VIDEO_DEVICE](state, video) {
    Object.assign(state.video, video)
  },
  [SETTINGS.SET_RECORD](state, recordInfo) {
    Object.assign(state.localRecordInfo, recordInfo)
  },
  [SETTINGS.SET_ALLOW](state, allow) {
    Object.assign(state.allow, allow)
  },

  // [SETTINGS.SET_VIDEO_DEVICE](state, videoDevice) {
  //   state.videoDevice = videoDevice
  // },
  [SETTINGS.SET_LANGUAGE](state, language) {
    state.language = language
  },

  [SETTINGS.SET_SCREEN_STREAM](state, screenStream) {
    state.screenStream = screenStream
  },

  [SETTINGS.SET_LOCAL_RECORD_TARGET](state, localRecordTarget) {
    state.localRecordTarget = localRecordTarget
  },

  [SETTINGS.SET_LCOAL_RECORD_STAUTS](state, localRecordStatus) {
    state.localRecordStatus = localRecordStatus
  },

  [TOGGLE_CHAT](state, flag) {
    state.chatBox = flag
  },
}
const getters = {
  mic: state => state.mic,
  speaker: state => state.speaker,
  video: state => state.video,
  localRecord: state => state.localRecordInfo,
  allow: state => state.allow,
  allowLocalRecord: state => state.allow.localRecording,
  allowPointing: state => state.allow.pointing,
  language: state => state.language,

  //screen stream for local recording
  screenStream: state => state.screenStream,

  // used Remote.js
  settingInfo: state => {
    return {
      mic: state.mic.deviceId,
      micOn: state.mic.isOn,
      video: state.video.deviceId,
    }
  },

  chatBox: state => state.chatBox,
}

const actions = {
  toggleChat({ commit }, flag) {
    commit('TOGGLE_CHAT', flag)
  },
}
export default {
  state,
  mutations,
  getters,
  actions,
}
