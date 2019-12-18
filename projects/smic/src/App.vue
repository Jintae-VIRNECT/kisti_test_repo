<template lang="pug">
	#app
		the-top-nav
		.body-wrapper
			router-view
		the-user-modal
</template>

<script>
import TheTopNav from '@/components/common/TheTopNav.vue'
import TheUserModal from '@/components/common/TheUserModal.vue'

export default {
	components: {
		TheTopNav,
		TheUserModal,
	},
}
</script>
