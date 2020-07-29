import { INIT_WORKSPACE, CHANGE_WORKSPACE } from '../mutation-types'

const setWorkspaceObj = info => {
  return {
    uuid: info.workspaceId,
    title: info.workspaceName,
    profile: info.workspaceProfile,
    renewalDate: info.renewalDate,
  }
}

const state = {
  current: {},
  workPlan: [],
  workspaceList: [],
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

export default {
  state,
  mutations,
}
