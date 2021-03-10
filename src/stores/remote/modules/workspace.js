import {
  INIT_WORKSPACE,
  CHANGE_WORKSPACE,
  CLEAR_WORKSPACE,
  SET_COMPANY_INFO,
} from '../mutation-types'
import { PLAN_STATUS } from 'configs/status.config'

const expireCheck = (time, planStatus) => {
  if (process.env.NODE_ENV !== 'production') return true
  const diff = new Date(time).getTime() - Date.now()
  return diff > 0 || planStatus === PLAN_STATUS.INACTIVE
}
const setWorkspaceObj = info => {
  return {
    uuid: info.workspaceId,
    title: info.workspaceName,
    profile: info.workspaceProfile,
    renewalDate: info.renewalDate,
    role: info.role,
    expire: !expireCheck(info.renewalDate, info.productPlanStatus),
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
  workPlan: [],
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
        }
      }
    }
  },
  [CHANGE_WORKSPACE](state, workspace) {
    state.current = workspace
    window.localStorage.setItem('workspace', workspace.uuid)
  },
  [CLEAR_WORKSPACE](state) {
    state.current = {}
  },
  [SET_COMPANY_INFO](state, payload) {
    for (let key in payload) {
      state.companyInfo[key] = payload[key]
    }
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

  useServerRecord: state => state.companyInfo.recording,
  useLocalRecord: state => state.companyInfo.localRecording,
  useStorage: state => state.companyInfo.storage,
}

export default {
  state,
  getters,
  mutations,
}
