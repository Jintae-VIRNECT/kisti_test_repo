import Vue from 'vue'
import Router from 'vue-router'

Vue.use(Router)

import HomeRouter from './partials/home'
import QRRouter from './partials/qr'
import PageNotFound from 'WC-Modules/vue/components/errors/404'
import InternetNotFound from 'WC-Modules/vue/components/errors/504'

export default new Router({
	name: 'router',
	mode: 'history',
	routes: [
		HomeRouter,
		QRRouter,
		{
			path: '/',
			redirect: {
				name: 'login',
			},
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
