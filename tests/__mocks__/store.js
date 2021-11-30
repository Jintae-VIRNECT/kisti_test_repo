import Vuex from 'vuex'

//unit-test에 사용될 mock store를 정의

let state
let getters
let actions
let mutations
let store

state = {}
getters = {}
actions = {}
mutations = {}
store = new Vuex.Store({
  state,
  getters,
  actions,
  mutations,
})

export default store
