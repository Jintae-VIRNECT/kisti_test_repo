<template lang="pug">
  div
    page-header
    page-tab-nav
      template(slot="page-nav--right")
        .search-wrapper.text-right
          el-input.tool.search(placeholder='공정 이름, 담당자 이름' v-model='search' )
            el-button(slot='append' icon='el-icon-search')
          span 필터 : 
          el-dropdown.tool.filter
            el-button(type='primary')
              | 전체
              i.el-icon-arrow-down.el-icon--right
            el-dropdown-menu(slot='dropdown')
              el-dropdown-item 전체
              el-dropdown-item 공정 진행 상태 별
          span 정렬 : 
          el-dropdown.tool.order
            el-button(type='primary')
              | ㄱ-ㅎ순
              i.el-icon-arrow-down.el-icon--right
            el-dropdown-menu(slot='dropdown')
              el-dropdown-item 최신 보고순
              el-dropdown-item 오래된 보고순
              el-dropdown-item 최신 등록순
              el-dropdown-item 오래된 등록순
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
