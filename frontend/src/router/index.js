import Vue from 'vue'
import Router from 'vue-router'
import Login from 'components/layout/pages/Login'
import Terms from 'components/layout/pages/Terms'
import Register from 'components/layout/pages/Register.vue'
import User from 'components/layout/pages/User.vue'
import Profile from 'components/layout/pages/Profile.vue'

Vue.use(Router)

export default new Router({
	mode: 'history',
	routes: [
		{
			path: '/',
			name: 'login',
			component: Login,
		},
		{
			path: '/terms',
			name: 'terms',
			component: Terms,
		},
		{
			path: '/register',
			name: 'register',
			component: Register,
			props: true,
		},
		{
			path: '/user',
			name: 'user',
			component: User,
		},
		{
			path: '/profile',
			name: 'profile',
			component: Profile,
		},
	],
})
