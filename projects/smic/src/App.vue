<template lang="pug">
	#app
		the-sidebar(:menus="menus")
		.admin-container
			the-top-nav
			el-main.body-wrapper
				router-view
</template>

<script>
import TheSidebar from '@/components/layout/TheSidebar.vue'
import TheTopNav from '@/components/layout/TheTopNav.vue'

export default {
	components: {
		TheTopNav,
		TheSidebar,
	},
	data() {
		return {
			menus: [
				{
					path: '/members',
					label: '멤버 관리',
					image: require('@/assets/image/admin/ic-settings.svg'),
					subMenus: [
						{
							label: '멤버 생성',
							path: '/members/new',
						},
						{
							label: '멤버 목록',
							path: '/members',
						},
					],
				},
				{
					path: '/contents',
					label: '콘텐츠 관리',
					image: require('@/assets/image/admin/ic-organization.svg'),
					subMenus: [
						{
							label: '콘텐츠 생성',
							path: '/contents/new',
							// component: () => import('components/admin/groups/AdminGroup'),
						},
						{
							label: '콘텐츠 목록',
							path: '/contents',
							// component: () => import('components/admin/groups/AdminUser'),
						},
					],
				},
				{
					path: '/process',
					label: '공정 관리',
					image: require('@/assets/image/admin/ic-group-member.svg'),
					subMenus: [
						{
							label: '공정 생성',
							path: '/process/new',
							// component: () => import('components/admin/groups/AdminGroup'),
						},
						{
							label: '공정 목록',
							path: '/process',
							// component: () => import('components/admin/groups/AdminUser'),
						},
					],
				},
			],
		}
	},
}
</script>
