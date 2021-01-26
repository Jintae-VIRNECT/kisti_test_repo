import Vuex from 'vuex'

//unit-test에 사용될 mock store를 정의

let state
let getters
let actions
let mutations
let store

state = {}
getters = {}
actions = {
  setView: jest.fn(),
  addChat: jest.fn(),
}
mutations = {
  updateParticipant: jest.fn(),
  clearParticipants: () => {
    state.participants = [{}]
  },
}
store = new Vuex.Store({
  state,
  getters,
  actions,
  mutations,
})

export default store
