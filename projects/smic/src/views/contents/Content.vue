<template lang="pug">
  div
    page-header
    page-tab-nav
      template(slot="page-nav--right")
        searchTabNav.search-wrapper.text-right(placeholder="공정 이름, 담당자 이름" :search="search" :filter="filter" :sort="sort" @change="onChangeSearch")
    router-view
    pagination(v-if="isListPage" target="contents" :params="params")
</template>
<script>
import PageTabNav from '@/components/common/PageTabNav.vue'
import searchTabNav from '@/components/common/SearchTabNav.vue'
import Pagination from '@/components/common/Pagination.vue'
export default {
  components: {
    PageTabNav,
    searchTabNav,
    Pagination,
  },
  data() {
    return {
      search: '',
      params: {
        size: 8,
      },
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
            value: 'createdDate,desc',
            label: '최신 보고순',
          },
          {
            value: 'createdDate,asc',
            label: '오래된 보고순',
          },
        ],
        value: 'createdDate,desc',
      },
    }
  },
  computed: {
    isListPage() {
      return this.$route.path === '/contents'
    },
  },
  methods: {
    onChangeSearch: function(params) {
      this.params = {
        ...this.params,
        ...params,
      }
      this.$store.dispatch('getContentsList', this.params)
      this.$router.push('/contents')
    },
  },
  created() {
    this.$store.dispatch('getContentsList', this.params)
  },
}
</script>
