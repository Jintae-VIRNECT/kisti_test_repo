import UserScaffold from './src/main'

UserScaffold.install = function(Vue) {
	Vue.component(UserScaffold.name, UserScaffold)
}

export default UserScaffold
