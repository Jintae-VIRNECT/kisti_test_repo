import { createLocalVue } from '@vue/test-utils'
import Vuex from 'vuex'
import ElementUI from 'element-ui'
import path from 'path'
import glob from 'glob'

const localVue = createLocalVue()
localVue.use(Vuex)
localVue.use(ElementUI)

const mixins = glob.sync(path.join(__dirname, '../src/mixin/*.js'))
for (const mixin of mixins) {
  localVue.mixin(mixin)
}

export default localVue
