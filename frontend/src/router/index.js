import Vue from 'vue'
import Router from 'vue-router'
import Login from 'components/layout/pages/Login'
import Terms from 'components/layout/pages/Terms'
import Signup from 'components/layout/pages/Signup.vue'
import User from 'components/layout/pages/User.vue'
import Find from 'components/layout/pages/Find.vue'
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
			path: '/signup',
			name: 'signup',
			component: Signup,
			props: true,
		},
		{
			path: '/user',
			name: 'user',
			component: User,
		},
		{
			path: '/find',
			name: 'find',
			component: Find,
		},
		{
			path: '/profile',
			name: 'profile',
			component: Profile,
		},
	],
})
