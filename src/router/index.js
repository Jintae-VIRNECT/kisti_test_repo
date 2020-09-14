import Vue from 'vue'
import Router from 'vue-router'

Vue.use(Router)

import App from '@/App'
import HomeRouter from './partials/home'
import QRRouter from './partials/qr'
import PageNotFound from 'WC-Modules/vue/components/errors/CommonError'
import NetworkError from 'WC-Modules/vue/components/errors/NetworkError'

const router = new Router({
	name: 'router',
	mode: 'history',
	routes: [
		HomeRouter,
		QRRouter,
		{
			path: '/',
			name: 'app',
			component: App,
			redirect: {
				name: 'main',
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
	],
})

// router.beforeEach((to, from, next) => {
// 	if (from.name == null) {
// 		;(async () => {
// 			const res = await api.getUrls()
// 			Vue.prototype.$urls = res
// 			next()
// 		})()
// 	} else next()
// })

export default router
