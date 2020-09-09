import Vue from 'vue'
import Router from 'vue-router'
import api from 'api/axios'

Vue.use(Router)

import HomeRouter from './partials/home'
import QRRouter from './partials/qr'
import PageNotFound from 'WC-Modules/vue/components/errors/CommonError'
import NetworkError from 'WC-Modules/vue/components/errors/NetworkError'
import HealthCheck from 'components/layout/common/HealthCheck'

const router = new Router({
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
			name: 'NetworkError',
			component: NetworkError,
		},
		{
			path: '/healthcheck',
			name: 'healthcheck',
			component: HealthCheck,
		},
	],
})

router.beforeEach((to, from, next) => {
	if (from.name == null) {
		;(async () => {
			const res = await api.getUrls()
			Vue.prototype.$urls = res
			next()
		})()
	} else next()
})

export default router
