import Vue from 'vue'
import Router from 'vue-router'
import Login from 'components/layout/pages/Login'
import Terms from 'components/layout/pages/Terms'
import Signup from 'components/layout/pages/Signup'
import User from 'components/layout/pages/User'
import Find from 'components/layout/pages/Find'
import Profile from 'components/layout/pages/Profile'
import Complete from 'components/layout/pages/Complete'
import PageNotFound from 'components/layout/errors/404'
import InternetNotFound from 'components/layout/errors/504'

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
			props: true,
		},
		{
			path: '/find',
			name: 'find',
			component: Find,
			props: true,
		},
		{
			path: '/profile',
			name: 'profile',
			component: Profile,
		},
		{
			path: '/complete',
			name: 'complete',
			component: Complete,
		},
		{
			path: '*',
			name: 'pageNotFound',
			component: PageNotFound,
		},
		{
			path: '/504',
			name: 'InternetNotFound',
			component: InternetNotFound,
		},
	],
})
