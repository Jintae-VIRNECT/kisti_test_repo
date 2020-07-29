import { INIT_WORKSPACE, CHANGE_WORKSPACE } from '../mutation-types'

const expireCheck = time => {
  const diff = new Date(time).getTime() - Date.now()
  return diff > 0
}
const setWorkspaceObj = info => {
  return {
    uuid: info.workspaceId,
    title: info.workspaceName,
    profile: info.workspaceProfile,
    renewalDate: info.renewalDate,
    expire: !expireCheck(info.renewalDate),
  }
}

const state = {
  current: {},
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
      state.current = state.workspaceList[0]
    }
  },
  [CHANGE_WORKSPACE](state, workspace) {
    state.current = workspace
  },
}

const getters = {
  expireLicense: state => {
    if (!state.current.uuid) return false
    else return state.current.expire
  },
}

export default {
  state,
  getters,
  mutations,
}
