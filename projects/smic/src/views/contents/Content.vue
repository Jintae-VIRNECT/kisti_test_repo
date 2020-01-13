<template lang="pug">
  div
    page-header(submenu='콘텐츠')
    page-tab-nav
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
