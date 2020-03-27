import Vue from 'vue'
import Router from 'vue-router'
import Login from 'components/layout/pages/Login'
import Terms from 'components/layout/pages/Terms'
import Register from 'components/layout/pages/Register.vue'
import Profile from '../components/Profile.vue'

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
		},
		{
			path: '/profile',
			name: 'profile',
			component: Profile,
		},
	],
})
