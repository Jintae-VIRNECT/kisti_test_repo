import Vuex from 'vuex'
import auth from 'auth'
import plan from 'plan'
//unit-test에 사용될 mock store를 정의

let store
store = new Vuex.Store({
  modules: {
    auth,
    plan,
  },
})

export default store
