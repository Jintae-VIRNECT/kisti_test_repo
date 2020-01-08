import Vue from 'vue'
import VueRouter from 'vue-router'

import Home from '@/views/Home.vue'
import Member from '@/views/members/Member.vue'
import MemberList from '@/views/members/MemberList.vue'
import MemberNew from '@/views/members/MemberNew.vue'

import Content from '@/views/contents/Content.vue'
import ContentList from '@/views/contents/ContentList.vue'
import ContentDetail from '@/views/contents/ContentDetail.vue'
import ContentNew from '@/views/contents/ContentNew.vue'

import User from '@/views/User.vue'

import NotFound404 from '@/views/NotFound404.vue'

import store from '@/store'
// import EventBus from '@/utils/eventBus.js'

Vue.use(VueRouter)

const routes = [
	{
		path: '/',
		component: Home,
		meta: {
			requiresAuth: true,
		},
	},
	{
		path: '/users',
		component: User,
	},
	{
		path: '/members',
		component: Member,
		meta: {
			requiresAuth: true,
		},
		children: [
			{
				path: '',
				component: MemberList,
			},
			{
				path: 'new',
				component: MemberNew,
			},
		],
	},
	{
		path: '/contents',
		component: Content,
		meta: {
			requiresAuth: true,
		},
		children: [
			{
				path: '',
				component: ContentList,
			},
			{
				path: 'new',
				component: ContentNew,
			},
			{
				path: ':id',
				component: ContentDetail,
			},
		],
	},
	{
		path: '*',
		component: NotFound404,
	},
]

const router = new VueRouter({
	mode: 'history',
	// base: process.env.BASE_URL,
	routes,
})

router.beforeEach((to, from, next) => {
	if (from.path !== '/' && to.path === from.path) return

	// auth check
	const matched = to.matched.find(record => record.meta.requiresAuth)
	let destination = to.path
	console.log('matched : ', matched)
	if (matched) {
		const user = store.state.user
		console.log('user.isLoggedIn : ', user.isLoggedIn)
		if (user.isLoggedIn) {
			// 마지막 접근루트로 이동
			const lastAccessPath = store.getters.getLastAccessPath || to.path
			if (lastAccessPath) {
				destination = lastAccessPath
				store.commit('USER_SET_LAST_ACCESS_PATH', { path: null })
			}
		} else {
			Vue.swal.fire({
				type: 'error',
				title: '로그인이 필요합니다',
				toast: true,
				position: 'bottom-end',
				showConfirmButton: false,
				timer: 1500,
			})
			destination = '/users'
			store.commit('USER_SET_LAST_ACCESS_PATH', {
				path: to.path,
			})
		}
	}

	if (destination !== to.path) {
		next({
			path: destination || to.path,
		})
	} else {
		next()
	}
})

export default router
