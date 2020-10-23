import Vue from 'vue'
import Router from 'vue-router'

Vue.use(Router)

import App from '@/App'
import MainRouter from './main'
import PageNotFound from 'WC-Modules/vue/components/errors/CommonError'
import NetworkError from 'WC-Modules/vue/components/errors/NetworkError'

const router = new Router({
	name: 'router',
	mode: 'history',
	routes: [
		MainRouter,
		{
			path: '',
			component: App,
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
