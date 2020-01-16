<template lang="pug">
	#app
		div(v-if="getIsLoggedIn")
			the-sidebar(:menus="menus")
			.admin-container
				the-top-nav
				main.admin-body
					router-view
		div(v-else)
			.top-nav.border
				.top-nav__center
					router-link(to="/")
						img.logo-img(src="~@/assets/image/logo-smart-factory.png") 
					.divider
					label.workspace Workstation
			router-view
</template>

<script>
import TheSidebar from '@/components/layout/TheSidebar.vue'
import TheTopNav from '@/components/layout/TheTopNav.vue'
import { mapGetters } from 'vuex'

export default {
	components: {
		TheTopNav,
		TheSidebar,
	},
	data() {
		return {
			menus: [
				{
					path: '/',
					label: '홈',
					image: require('@/assets/image/ic-home.svg'),
				},
				{
					path: '/members',
					pathAlias: ['/members', '/contents', '/process'],
					label: '워크스페이스 관리',
					image: require('@/assets/image/ic-workspace-management.svg'),
				},
				{
					path: '/issue',
					label: '이슈 관리',
					image: require('@/assets/image/ic-issue.svg'),
				},
			],
		}
	},
	computed: {
		...mapGetters(['getIsLoggedIn']),
	},
}
</script>
