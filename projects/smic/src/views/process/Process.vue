<template lang="pug">
  div
    page-header(submenu='공정')
    page-tab-nav
    div
      router-link(to='/process/new')
        el-button(type='primary' tag="el-button") 공정 등록
      router-link(to='/process')
        el-button(type='primary' tag="el-button") 전체 목록
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
