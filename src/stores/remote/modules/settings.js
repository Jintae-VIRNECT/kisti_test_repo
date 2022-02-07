//Definition of workspace store
import {
  SETTINGS,
  TOGGLE_CHAT,
  SET_CHAT_ACTIVE,
  ALLOW_RESET,
  SET_SPOT_FULLSCREEN,
} from '../mutation-types'
import { RECORD_TARGET } from 'utils/recordOptions'
import { resolution } from 'utils/settingOptions'
import { FLASH as FLASH_STATUS } from 'configs/device.config'

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
    fps: 30,
  },
  flash: FLASH_STATUS.FLASH_NONE,
  localRecordInfo: {
    time: '60',
    interval: '1',
    resolution: '480p',
  },
  serverRecordInfo: {
    time: '30',
    resolution: '720p',
  },
  allow: {
    pointing: true,
    localRecord: true,
  },
  useScreenStrict: false,

  translate: {
    flag: false,
    code: 'ko-KR',
    multiple: true,
    sttSync: true,
    ttsAllow: false,
  },

  localRecordTarget: RECORD_TARGET.WORKER,
  localRecordStatus: 'STOP', // 'START', 'STOP'
  serverRecordStatus: 'STOP', // 'WAIT', 'START', 'STOP', 'PREPARE'

  chatBox: false,
  chatActive: false,

  isSpotFullscreen: false,
  autoServerRecord: false,
}

const mutations = {
  [SETTINGS.SET_FLASH_DEVICE](state, flash) {
    state.flash = flash
  },
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
  [SETTINGS.SET_SERVER_RECORD](state, recordInfo) {
    Object.assign(state.serverRecordInfo, recordInfo)
  },
  [SETTINGS.SET_ALLOW](state, allow) {
    Object.assign(state.allow, allow)
  },
  [ALLOW_RESET](state) {
    state.allow = {
      pointing: true,
      localRecord: true,
    }
  },

  [SETTINGS.SET_TRANSLATE_FLAG](state, flag) {
    state.translate.flag = flag
  },
  [SETTINGS.SET_TRANSLATE_CODE](state, code) {
    state.translate.code = code
  },

  [SETTINGS.SET_TRANSLATE_MULTIPLE](state, flag) {
    state.translate.multiple = flag
  },
  [SETTINGS.SET_STT_SYNC](state, code) {
    state.translate.sttSync = code
  },
  [SETTINGS.SET_TTS_ALLOW](state, flag) {
    state.translate.ttsAllow = flag
  },
  [SETTINGS.SET_SCREEN_STRICT](state, flag) {
    state.useScreenStrict = flag
  },

  [SETTINGS.SET_LOCAL_RECORD_TARGET](state, localRecordTarget) {
    state.localRecordTarget = localRecordTarget
  },

  [SETTINGS.SET_LOCAL_RECORD_STATUS](state, localRecordStatus) {
    state.localRecordStatus = localRecordStatus
  },

  [SETTINGS.SET_SERVER_RECORD_STATUS](state, serverRecordStatus) {
    state.serverRecordStatus = serverRecordStatus
  },

  [TOGGLE_CHAT](state, flag) {
    state.chatBox = flag
  },

  [SET_CHAT_ACTIVE](state, active) {
    state.chatActive = active
  },

  [SET_SPOT_FULLSCREEN](state, isFullscreen) {
    state.isSpotFullscreen = isFullscreen
  },

  [SETTINGS.SET_AUTO_SERVER_RECORD](state, flag) {
    state.autoServerRecord = flag
  },
}
const getters = {
  mic: state => state.mic,
  speaker: state => state.speaker,
  video: state => state.video,
  flash: state => state.flash,
  localRecord: state => state.localRecordInfo,
  serverRecord: state => state.serverRecordInfo,
  allowLocalRecord: state => state.allow.localRecord,
  allowPointing: state => state.allow.pointing,

  translate: state => state.translate,
  useScreenStrict: state => state.useScreenStrict,

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
      fps: state.video.fps,
    }
  },

  chatBox: state => state.chatBox,
  chatActive: state => state.chatActive,

  isSpotFullscreen: state => state.isSpotFullscreen,

  autoServerRecord: state => state.autoServerRecord,
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
