import {
  INIT_WORKSPACE,
  CHANGE_WORKSPACE,
  CLEAR_WORKSPACE,
  SET_COMPANY_INFO,
  CLEAR_COMPANY_INFO,
} from '../mutation-types'
import { PLAN_STATUS } from 'configs/status.config'

const setWorkspaceObj = info => {
  return {
    uuid: info.uuid,
    title: info.name,
    profile: info.profile,
    renewalDate: info.renewalDate,
    role: info.role,
    planStatus: info.productPlanStatus,
    expire: info.productPlanStatus === PLAN_STATUS.INACTIVE,
  }
}
const companyInfo = {
  companyCode: 0, // 회사 코드
  translation: false, // 번역기능
  tts: false, // TTS
  sttSync: false, // STT 동기
  sttStreaming: false, // STT 스트리밍
  recording: false, // 서버녹화
  storage: false, // 파일 업/다운로드
  sessionType: 'PRIVATE', // 오픈방 유무 (PRIVATE, OPEN, PUBLIC)
  licenseName: '',
  languageCodes: [],
  localRecording: false,
  audioRestrictedMode: false,
  videoRestrictedMode: false,
}

const state = {
  current: {},
  companyInfo: companyInfo,
  workspaceList: [
    // {
    //   planProduct: 'REMOTE',
    //   renewalDate: '2020-08-01T07:04:29',
    //   uuid: '462811bf3a8adfc49b121c8d7617e5e3',
    //   title: '1111111111111',
    //   profile:
    //     'http://192.168.6.3:8082/workspaces/upload/workspace-profile.png',
    //   expire: true,
    // },
    // {
    //   planProduct: 'REMOTE',
    //   renewalDate: '2020-08-01T07:04:29',
    //   uuid: '4bdebc670244f7c886ace5340ea01fa7',
    //   title: '왕2222222222222222222',
    //   profile:
    //     'http://192.168.6.3:8082/workspaces/upload/workspace-profile.png',
    //   expire: false,
    // },
  ],
}

const mutations = {
  [INIT_WORKSPACE](state, infoList) {
    state.workspaceList = []
    for (let workspace of infoList) {
      state.workspaceList.push(setWorkspaceObj(workspace))
    }
    if (state.workspaceList.length > 0) {
      const workspaceId = window.localStorage.getItem('workspace')
      if (workspaceId) {
        let idx = state.workspaceList.findIndex(
          work => work.uuid === workspaceId,
        )
        if (idx > -1) {
          state.current = state.workspaceList[idx]
          window.localStorage.setItem(
            'workspace',
            state.workspaceList[idx].uuid,
          )
        }
      } else {
        if (state.workspaceList.length === 1) {
          state.current = state.workspaceList[0]
        }
      }
    }
  },
  [CHANGE_WORKSPACE](state, workspace) {
    state.current = workspace
    window.localStorage.setItem('workspace', workspace.uuid)
  },
  [CLEAR_WORKSPACE](state, uuid) {
    if (uuid) {
      const idx = state.workspaceList.findIndex(
        workspace => workspace.uuid === uuid,
      )
      if (idx > -1) {
        state.workspaceList.splice(idx, 1)
        window.localStorage.setItem('workspace', null)
      }
    }
    state.current = {}
  },
  [SET_COMPANY_INFO](state, payload) {
    for (let key in payload) {
      state.companyInfo[key] = payload[key]
    }
  },
  [CLEAR_COMPANY_INFO](state) {
    state.companyInfo = companyInfo
  },
}

const getters = {
  expireLicense: state => {
    if (!state.current.uuid) return false
    else return state.current.expire
  },
  workspace: state => state.current,
  workspaceList: state => state.workspaceList,
  targetCompany: state => state.companyInfo.companyCode,
  useTranslate: state => state.companyInfo.translation,
  useOpenRoom: state => {
    return state.companyInfo.sessionType === 'OPEN'
  },
  languageCodes: state => state.companyInfo.languageCodes,
  useLocalRecording: state => state.companyInfo.localRecording,
  useRecording: state => state.companyInfo.recording,
  useStorage: state => state.companyInfo.storage,
  restrictedMode: state => {
    return {
      audio: state.companyInfo.audioRestrictedMode,
      video: state.companyInfo.videoRestrictedMode,
    }
  },
}

export default {
  state,
  getters,
  mutations,
}
