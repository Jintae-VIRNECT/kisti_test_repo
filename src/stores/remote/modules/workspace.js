import { INIT_WORKSPACE, CHANGE_WORKSPACE } from '../mutation-types'

class Workspace {
  constructor(info) {
    this.uuid = info.uuid
    this.description = info.description
    this.pinNumber = info.pinNumber
    this.role = info.role
    this.updateDate = info.updateDate
  }
  getUuid() {
    return this.uuid
  }
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
    console.log(state.workspaceList)
    if (state.workspaceList.length > 0) {
      let mySelection = state.workspaceList.findIndex(
        workspace => workspace.uuid === '40f9bbee9d85dca7a34a0dd205aae718',
      )
      if (mySelection > -1) {
        state.current = state.workspaceList[mySelection]
      } else {
        state.current = state.workspaceList[0]
      }
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
