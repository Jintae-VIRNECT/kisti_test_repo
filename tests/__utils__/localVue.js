import { createLocalVue } from '@vue/test-utils'

import Vue from 'vue'
import Vuex from 'vuex'
import ElementUI from 'element-ui'

const localVue = createLocalVue()

const EventBus = new Vue()
const GlobalPlugins = {
  install(v) {
    v.prototype.$eventBus = EventBus
  },
}

localVue.prototype.$eventBus = createLocalVue()
localVue.use(GlobalPlugins)
localVue.use(Vuex)
localVue.use(ElementUI)

export default localVue
