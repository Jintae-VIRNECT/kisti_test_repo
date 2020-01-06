<template lang="pug">
	#app
		the-sidebar(:menus="menus")
		.admin-container
			the-top-nav
			el-main.body-wrapper
				router-view
		the-user-modal
</template>

<script>
import TheSidebar from '@/components/layout/TheSidebar.vue'
import TheTopNav from '@/components/layout/TheTopNav.vue'
import TheUserModal from '@/components/common/TheUserModal.vue'
import menus from '@/mixins/menus'

export default {
	components: {
		TheTopNav,
		TheUserModal,
		TheSidebar,
	},
	mixins: [menus],
}
</script>
