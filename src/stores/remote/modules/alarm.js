import { ADD_ALARM, REMOVE_ALARM, UPDATE_ALARM } from '../mutation-types'

const state = {
  alarmList: [],
}

const mutations = {
  [ADD_ALARM](state, alarm) {
    state.alarmList.splice(0, 0, {
      ...alarm,
    })
  },
  [REMOVE_ALARM](state, id) {
    const idx = state.alarmList.findIndex(alarm => alarm.id === id)
    if (idx < 0) return
    state.alarmList.splice(idx, 1)
  },
  [UPDATE_ALARM](state, info) {
    const idx = state.alarmList.findIndex(alarm => alarm.id === info.id)
    if (idx < 0) return
    for (let key in info) {
      if (key === 'id') continue
      state.alarmList[idx][key] = info[key]
    }
  },
}

const getters = {
  alarmList: state => state.alarmList,
}

const actions = {
  addAlarm({ commit }, payload) {
    commit(ADD_ALARM, payload)
  },
  removeAlarm({ commit }, payload) {
    commit(REMOVE_ALARM, payload)
  },
  updateAlarm({ commit }, payload) {
    commit(UPDATE_ALARM, payload)
  },
}

export default {
  state,
  getters,
  actions,
  mutations,
}
