import directive from './src/directive'
import mixin from './src/mixin'

export default {
	install(Vue) {
		Vue.directive('validate', directive.validate)

		Vue.mixin(mixin)
	},
}
