import { INIT_WORKSPACE, CHANGE_WORKSPACE } from '../mutation-types'

function Workspace(info) {
  this.uuid = info.uuid
  this.description = info.description
  this.pinNumber = info.pinNumber
  this.role = info.role
  this.updateDate = info.updateDate
}

const state = {
  current: {},
  workspaceList: [],
}

const mutations = {
  [INIT_WORKSPACE](state, infoList) {
    state.workspaceList = []
    for (let space of infoList) {
      state.workspaceList.push(new Workspace(space))
    }
    if (state.workspaceList.length > 0) {
      state.current = state.workspaceList[0]
      console.log('Current Workspace > ', state.current)
    }
  },
  [CHANGE_WORKSPACE](state, workspaceId) {
    const choice = state.workspaceList.find(
      workspace => workspace.getUuid() === workspaceId,
    )
    state.current = choice
  },
}

export default {
  state,
  mutations,
}
