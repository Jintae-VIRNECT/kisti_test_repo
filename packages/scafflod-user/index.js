import UserScaffold from './src/main'

/* istanbul ignore next */
UserScaffold.install = function(Vue) {
	Vue.component(UserScaffold.name, UserScaffold)
}

export default UserScaffold
