import Vue from 'vue'
import Router from 'vue-router'

Vue.use(Router)

import HomeRouter from './partials/home'
import QRRouter from './partials/qr'
import PageNotFound from 'WC-Modules/vue/components/errors/CommonError'
import InternetNotFound from 'WC-Modules/vue/components/errors/NetworkError'
import HealthCheck from 'components/layout/common/HealthCheck'

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
		{
			path: '/healthcheck',
			name: 'healthcheck',
			component: HealthCheck,
		},
	],
})
