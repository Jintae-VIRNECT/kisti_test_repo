<template lang="pug">
	div
		page-header
		router-view
</template>
<script>
import PageTabNav from '@/components/common/PageTabNav.vue'
import PageHeader from '@/components/common/PageHeader.vue'
export default {
  components: {
    PageTabNav,
    PageHeader,
  },
}
</script>
