<template lang="pug">
	#app
		the-sidebar
		.admin-container
			the-top-nav
			.body-wrapper
				router-view
		the-user-modal
</template>

<script>
import TheSidebar from '@/components/layout/TheSidebar.vue'
import TheTopNav from '@/components/layout/TheTopNav.vue'
import TheUserModal from '@/components/common/TheUserModal.vue'

export default {
	components: {
		TheTopNav,
		TheUserModal,
		TheSidebar,
	},
}
</script>
