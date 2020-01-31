<template lang="pug">
  div
    page-header
    page-tab-nav
      template(slot="page-nav--right")
        search-tab-nav.search-wrapper(placeholder="멤버 이름, ID" :filter="filter" :sort="sort" @onChangeSearch="onChangeSearch")
    router-view
</template>
<script>
import PageTabNav from '@/components/common/PageTabNav.vue'
import SearchTabNav from '@/components/common/SearchTabNav.vue'

export default {
  components: {
    PageTabNav,
    SearchTabNav,
  },
  data() {
    return {
      searchInput: null,
      filter: {
        options: [
          {
            value: 'All',
            label: '전체',
          },
          {
            value: 'Master',
            label: '마스터',
          },
          {
            value: 'Member',
            label: '멤버',
          },
        ],
        value: 'All',
      },
      sort: {
        options: [
          {
            value: 'alphabetDesc',
            label: 'ㄱ-ㅎ순',
          },
          {
            value: 'alphabetAsc',
            label: 'ㄱ-ㅎ역순',
          },
        ],
        value: 'alphabetDesc',
      },
    }
  },
  methods: {
    onChangeSearch: function() {
      this.$emit('getUserList')
    },
  },
}
</script>
