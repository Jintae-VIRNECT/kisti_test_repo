import { createLocalVue } from '@vue/test-utils'

import Vue from 'vue'
import Vuex from 'vuex'

import Vue2Scrollbar from 'plugins/remote/scrollbar'
// import VueToasted from 'plugins/remote/toasted'
// import Alarm from 'plugins/remote/alarm'
// import Store from 'stores/remote/store'
import globalMixin from 'mixins/global'

const localVue = createLocalVue()

const EventBus = new Vue()
const GlobalPlugins = {
  install(v) {
    v.prototype.$eventBus = EventBus
  },
}

localVue.prototype.$eventBus = createLocalVue()
localVue.use(GlobalPlugins)
localVue.use(Vue2Scrollbar)
localVue.use(Vuex)
// localVue.use(Alarm)
localVue.mixin(globalMixin)

export default localVue
