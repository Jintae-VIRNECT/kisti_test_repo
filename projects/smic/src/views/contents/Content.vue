<template lang="pug">
  div
    page-header(submenu='콘텐츠')
    page-tab-nav
    page-bread-crumb(title='콘텐츠')
    router-view
</template>
<script>
import PageTabNav from '@/components/common/PageTabNav.vue'
import PageBreadCrumb from '@/components/common/PageBreadCrumb.vue'
export default {
	components: {
		PageTabNav,
		PageBreadCrumb,
	},
}
</script>
