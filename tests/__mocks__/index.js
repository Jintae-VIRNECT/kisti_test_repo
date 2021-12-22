import localVue from '../__utils__/localVue'
import store from './store'
import routes from './routes'

import ElementUI from 'element-ui'
import VeeValidate from 'vee-validate'
localVue.use(ElementUI)
localVue.use(VeeValidate)

export default { localVue, store, routes }
