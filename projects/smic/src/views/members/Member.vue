<template lang="pug">
  div
    page-header
    page-tab-nav
      template(slot="page-nav--right")
        search-tab-nav.search-wrapper(placeholder="멤버 이름, ID" :search="search" :filter="filter" :sort="sort" @change="onChangeSearch")
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
      search: '',
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
        value: ['All'],
      },
      sort: {
        options: [
          {
            value: 'name,asc',
            label: 'ㄱ-ㅎ순',
          },
          {
            value: 'name,desc',
            label: 'ㄱ-ㅎ역순',
          },
        ],
        value: 'name,asc',
      },
    }
  },
  methods: {
    onChangeSearch: function({ searchInput, filterValue, sortValue }) {
      this.$store.dispatch('getMemberList', {
        search: searchInput,
        filter: filterValue.map(value => value.toUpperCase()).join(),
        sort: sortValue,
      })
    },
  },
}
</script>
