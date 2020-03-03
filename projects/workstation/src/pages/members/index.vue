<template>
  <div>
    <page-header></page-header>
    <page-tab-nav>
      <template slot="page-nav--right">
        <search-tab-nav
          class="search-wrapper"
          placeholder="멤버 이름, ID"
          :search="search"
          :filter="filter"
          :sort="sort"
          @change="onChangeSearch"
        >
        </search-tab-nav>
      </template>
    </page-tab-nav>
    <router-view></router-view>
    <!-- <pagination target="member" :params="params"></pagination> -->
  </div>
</template>
<script>
import PageTabNav from '@/components/common/PageTabNav.vue'
import SearchTabNav from '@/components/common/SearchTabNav.vue'
// import Pagination from '@/components/common/Pagination.vue'

export default {
  components: {
    PageTabNav,
    SearchTabNav,
    // Pagination,
  },
  data() {
    return {
      search: '',
      params: {
        size: 10,
      },
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
    onChangeSearch(params) {
      this.params = {
        ...this.params,
        ...params,
      }
      // sort 있으면 에러나는 문제
      if (this.params.filter !== 'ALL') {
        delete this.params.sort
      }
      this.$store.dispatch('getMemberList', this.params)
    },
  },
  async created() {
    await this.$store.dispatch('getMemberList', this.params)
  },
}
</script>
