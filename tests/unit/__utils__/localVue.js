import { createLocalVue } from '@vue/test-utils'

import Vue from 'vue'
import Vuex from 'vuex'

import Store from 'stores/remote/store'

import Vue2Scrollbar from 'plugins/remote/scrollbar'

// import globalMixin from 'mixins/global'

import VueToasted from 'plugins/remote/toasted'
import Alarm from 'plugins/remote/alarm'

import DayJS from 'plugins/remote/dayjs'
import call from 'plugins/remote/call'

// import i18n from 'plugins/remote/i18n'

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
localVue.use(VueToasted)
localVue.use(DayJS)
localVue.use(Alarm)
// localVue.mixin(globalMixin)
localVue.use(call, { Store })

export default localVue
