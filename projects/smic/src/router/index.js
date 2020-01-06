import Vue from 'vue'
import VueRouter from 'vue-router'

import Home from '@/views/Home.vue'
import Member from '@/views/Member.vue'
import MemberList from '@/components/members/MemberList.vue'
import MemberForm from '@/components/members/MemberForm.vue'

import Content from '@/views/Content.vue'
import ContentList from '@/components/contents/ContentList.vue'
import ContentDetail from '@/components/contents/ContentDetail.vue'

import User from '@/views/User.vue'
// import UserSignIn from '@/components/user/UserSignIn.vue'
// import UserSignUp from '@/components/user/UserSignUp.vue'

import NotFound404 from '@/views/NotFound404.vue'

import store from '@/store'
// import EventBus from '@/utils/eventBus.js'

Vue.use(VueRouter)

const routes = [
	{
		path: '/',
		component: Home,
		// meta: {
		// 	requiresAuth: true,
		// },
	},
	{
		path: '/user',
		component: User,
		// children: [
		// 	{
		// 		path: '/sign_in',
		// 		component: UserSignIn,
		// 	},
		// 	{
		// 		path: '/sign_up',
		// 		component: UserSignUp,
		// 	},
		// ],
	},
	{
		path: '/members',
		component: Member,
		children: [
			{
				path: '',
				component: MemberList,
			},
			{
				path: 'add',
				component: MemberForm,
			},
		],
	},
	{
		path: '/contents',
		component: Content,
		children: [
			{
				path: 'list',
				alias: '',
				component: ContentList,
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
	if (matched) {
		const user = store.state.user
		if (user.isLoggedIn && store.getters.getAuth === matched.meta.auth) {
			// 마지막 접근루트로 이동
			const lastAccessPath = store.getters.getLastAccessPath
			if (lastAccessPath) {
				destination = lastAccessPath
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
			destination = from.path
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
