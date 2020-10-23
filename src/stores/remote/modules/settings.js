//Definition of workspace store
import { SETTINGS, TOGGLE_CHAT } from '../mutation-types'
import { RECORD_TARGET, LCOAL_RECORD_STAUTS } from 'utils/recordOptions'
import { resolution } from 'utils/settingOptions'

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
    quality: '720',
  },
  localRecordInfo: {
    time: '60',
    interval: '1',
    resolution: '480p',
  },
  allow: {
    pointing: true,
    localRecord: true,
  },

  videoDevice: null,
  language: null,

  translate: {
    flag: false,
    code: 'ko-KR',
  },

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

  [SETTINGS.SET_TRANSLATE_FLAG](state, flag) {
    state.translate.flag = flag
  },
  [SETTINGS.SET_TRANSLATE_CODE](state, code) {
    state.translate.code = code
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
  allowLocalRecord: state => state.allow.localRecord,
  allowPointing: state => state.allow.pointing,
  language: state => state.language,

  translate: state => state.translate,

  //screen stream for local recording
  screenStream: state => state.screenStream,

  // used Remote.js
  settingInfo: state => {
    let idx = resolution.findIndex(res => res.value === state.video.quality)
    if (idx < 0) idx = 2
    return {
      mic: state.mic.deviceId,
      micOn: state.mic.isOn,
      video: state.video.deviceId,
      videoOn: state.video.isOn,
      quality: resolution[idx].resolution,
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
