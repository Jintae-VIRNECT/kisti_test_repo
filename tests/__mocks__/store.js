import Vuex from 'vuex'

const state = {}
const getters = {
  customInfo: jest.fn(),
}
const actions = {}
const mutations = {}
const store = new Vuex.Store({
  state,
  getters,
  actions,
  mutations,
})

export default store
