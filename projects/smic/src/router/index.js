import Vue from 'vue'
import VueRouter from 'vue-router'

import Home from '@/views/Home.vue'

import Post from '@/views/Post.vue'
import PostList from '@/components/posts/PostList.vue'
import PostDetail from '@/components/posts/PostDetail.vue'

import Member from '@/views/Member.vue'
import MemberList from '@/components/members/MemberList.vue'
import MemberDetail from '@/components/members/MemberDetail.vue'

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
		meta: {
			requiresAuth: true,
		},
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
		path: '/posts',
		component: Post,
		children: [
			{
				path: '',
				component: PostList,
			},
			{
				path: ':id',
				component: PostDetail,
				meta: {
					requiresAuth: true,
					auth: 'manager',
				},
			},
		],
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
				path: ':id',
				component: MemberDetail,
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
	base: process.env.BASE_URL,
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
