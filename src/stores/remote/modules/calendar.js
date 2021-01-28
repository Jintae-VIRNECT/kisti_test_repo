import { CALENDAR_SET } from '../mutation-types'

const state = {
  calendars: [], // name, date, status
}

const mutations = {
  [CALENDAR_SET](state, calendar) {
    const index = state.calendars.findIndex(cal => cal.name === calendar.name)
    if (index < 0) {
      state.calendars.push(calendar)
    } else {
      for (let key in calendar) {
        state.calendars[index][key] = calendar[key]
      }
    }
  },
}

export default {
  state,
  mutations,
}
