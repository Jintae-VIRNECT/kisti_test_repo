<template lang="pug">
  div
    page-header(submenu='공정')
    page-tab-nav
      template(slot='page-nav--right')
        router-link.enroll-new-process(to="/contents/new")
          el-button 신규 공정 등록
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
