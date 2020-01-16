<template lang="pug">
  div
    page-header(submenu='멤버')
    page-tab-nav
      template(slot="page-nav--right")
        .search-wrapper
          el-input.tool.search(placeholder='이름 또는 이메일 검색' v-model='search' )
            el-button(slot='append' icon='el-icon-search')
          span 필터 : 
          el-dropdown.tool.filter
            el-button(type='primary')
              | 전체
              i.el-icon-arrow-down.el-icon--right
            el-dropdown-menu(slot='dropdown')
              el-dropdown-item Action 1
              el-dropdown-item Action 2
              el-dropdown-item Action 3
              el-dropdown-item Action 4
              el-dropdown-item Action 5
          span 정렬 : 
          el-dropdown.tool.order
            el-button(type='primary')
              | ㄱ-ㅎ순
              i.el-icon-arrow-down.el-icon--right
            el-dropdown-menu(slot='dropdown')
              el-dropdown-item Action 1
              el-dropdown-item Action 2
              el-dropdown-item Action 3
              el-dropdown-item Action 4
              el-dropdown-item Action 5
    router-view
</template>
<script>
import PageTabNav from '@/components/common/PageTabNav.vue'
export default {
	components: {
		PageTabNav,
	},
	data() {
		return {
			search: null,
		}
	},
}
</script>
