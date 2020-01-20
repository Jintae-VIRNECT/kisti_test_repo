<template lang="pug">
	div
		page-header(submenu='공정')
		router-view
</template>
<script>
import PageTabNav from '@/components/common/PageTabNav.vue'
export default {
	components: {
		PageTabNav,
	},
}
</script>
