import {
  INIT_WORKSPACE,
  CHANGE_WORKSPACE,
  CLEAR_WORKSPACE,
  SET_COMPANY_INFO,
  CLEAR_COMPANY_INFO,
} from '../mutation-types'
import { PLAN_STATUS } from 'configs/status.config'
import Store from 'stores/remote/store'
import { validJsonString } from 'utils/regexp'

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
  timeout: 60, //협업 연장 질의 팝업 싸이클을 정하는 값. 분 단위(기본 한시간)
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
    //     'http://192.168.0.9:8082/workspaces/upload/workspace-profile.png',
    //   expire: true,
    // },
    // {
    //   planProduct: 'REMOTE',
    //   renewalDate: '2020-08-01T07:04:29',
    //   uuid: '4bdebc670244f7c886ace5340ea01fa7',
    //   title: '왕2222222222222222222',
    //   profile:
    //     'http://192.168.0.9:8082/workspaces/upload/workspace-profile.png',
    //   expire: false,
    // },
  ],
}

const mutations = {
  [INIT_WORKSPACE](state, infoList) {
    //fetch해온 사용자 보유 워크스페이스 목록 초기화
    state.workspaceList = []
    for (let workspace of infoList) {
      state.workspaceList.push(setWorkspaceObj(workspace))
    }

    //사용자가 보유한 워크스페이스가 있는 경우
    if (state.workspaceList.length > 0) {
      const workspaceInfo =
        validJsonString(window.localStorage.getItem('workspace')) || {}
      const workspaceId = workspaceInfo?.[Store.getters.account.uuid] //해당 유저의 워크스페이스 정보 확인

      //localstorage에 저장된 workspace 정보가 있는 경우
      if (workspaceId) {
        //현재 보유 워크스페이스 목록에서 저장되있던 워크스페이스를 검색
        let idx = state.workspaceList.findIndex(
          work => work.uuid === workspaceId,
        )
        //있는 경우 현재 워크스페이스를 해당 워크스페이스로 설정 및 localstorage 재저장
        if (idx > -1) {
          state.current = state.workspaceList[idx]
          workspaceInfo[Store.getters.account.uuid] =
            state.workspaceList[idx].uuid //해당 유저 워크스페이스 업데이트
          window.localStorage.setItem(
            'workspace',
            JSON.stringify(workspaceInfo),
          )
        }
      }
      //없는 경우 : 보유한 워크스페이스가 하나뿐인 경우 현재 워크스페이스로 설정하고,
      //하나 이상인 경우에는 유저가 직접 선택하게된다.
      else {
        if (state.workspaceList.length === 1) {
          state.current = state.workspaceList[0]
        }
      }
    }
  },
  [CHANGE_WORKSPACE](state, workspace) {
    state.current = workspace
    const workspaceInfo =
      validJsonString(window.localStorage.getItem('workspace')) || {}
    workspaceInfo[Store.getters.account.uuid] = workspace.uuid //해당 유저 워크스페이스 업데이트
    window.localStorage.setItem('workspace', JSON.stringify(workspaceInfo))
  },
  [CLEAR_WORKSPACE](state, uuid) {
    if (uuid) {
      const idx = state.workspaceList.findIndex(
        workspace => workspace.uuid === uuid,
      )
      if (idx > -1) {
        state.workspaceList.splice(idx, 1)
        const workspaceInfo =
          validJsonString(window.localStorage.getItem('workspace')) || {}
        delete workspaceInfo[Store.getters.account.uuid] //해당 유저 워크스페이스 정보 제거
        window.localStorage.setItem('workspace', JSON.stringify(workspaceInfo))
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
  coworkTimeout: state => state.companyInfo.timeout, //협업 연장 질의 팝업 생성 사이클. 분
}

export default {
  state,
  getters,
  mutations,
}
