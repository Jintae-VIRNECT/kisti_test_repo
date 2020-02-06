<template lang="pug">
  div
    page-header
    page-tab-nav
      template(slot="page-nav--right")
        searchTabNav.search-wrapper.text-right(placeholder="공정 이름, 담당자 이름" :search="search" :filter="filter" :sort="sort" @change="onChangeSearch")
    router-view
</template>
<script>
import PageTabNav from '@/components/common/PageTabNav.vue'
import searchTabNav from '@/components/common/SearchTabNav.vue'
export default {
  components: {
    PageTabNav,
    searchTabNav,
  },
  data() {
    return {
      search: '',
      filter: {
        options: [
          {
            value: 'All',
            label: '전체',
          },
          {
            value: '?',
            label: '공정 진행 상태 별',
          },
        ],
        value: ['All'],
      },
      sort: {
        options: [
          {
            value: '1',
            label: '최신 보고순',
          },
          {
            value: '2',
            label: '오래된 보고순',
          },
          {
            value: '3',
            label: '최신 등록순',
          },
          {
            value: '4',
            label: '오래된 등록순',
          },
        ],
        value: '1',
      },
    }
  },
  methods: {
    onChangeSearch() {
      this.$store.dispatch('CONTENTS_LIST')
    },
  },
}
</script>
