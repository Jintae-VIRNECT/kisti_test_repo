<template lang="pug">
  div
    page-header
    page-tab-nav
      template(slot="page-nav--right")
        searchTabNav.search-wrapper.text-right(placeholder="공정 이름, 담당자 이름" :search="params.search" :filter="filter" :sort="sort" @change="onChangeSearch")
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
      params: {
        search: '',
        size: 8,
      },
      filter: {
        options: [
          {
            value: 'All',
            label: '전체',
          },
          {
            value: 'PUBLISH',
            label: '공정 등록',
          },
          {
            value: 'WAIT',
            label: '공정 미등록',
          },
        ],
        value: ['All'],
      },
      sort: {
        options: [
          {
            value: 'createdDate,desc',
            label: '최신 등록순',
          },
          {
            value: 'createdDate,asc',
            label: '오래된 등록순',
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
    // route query check
    const query = this.$router.currentRoute.query
    if (query && query.search) {
      this.params.search = query.search
    }
    this.$store.dispatch('getContentsList', this.params)
  },
}
</script>
