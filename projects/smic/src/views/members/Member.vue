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
import Pagination from '@/components/common/Pagination.vue'

export default {
  components: {
    PageTabNav,
    SearchTabNav,
    Pagination,
  },
  data() {
    return {
      search: '',
      params: {},
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
    onChangeSearch({ searchInput, filterValue, sortValue }) {
      this.params = {
        search: searchInput,
        filter: filterValue.map(value => value.toUpperCase()).join(),
        sort: sortValue,
      }
      this.$store.dispatch('getMemberList', this.params)
    },
  },
  async created() {
    await this.$store.dispatch('getMemberList')
  },
}
</script>
