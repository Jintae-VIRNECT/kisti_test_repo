<template>
  <div id="issues">
    <h2>Issues</h2>
    <search-tab-nav
      :filter="issueFilter"
      :sort="issueSort"
      @submit="searchIssues"
    />
    <p v-for="issue in issueList" :key="issue.id">
      <nuxt-link :to="`/issues/${issue.id}`">{{ issue }}</nuxt-link>
    </p>
    <el-row type="flex" justify="center">
      <el-pagination
        background
        layout="prev, pager, next"
        :total="issueTotal"
        @current-change="issuePageChange"
      />
    </el-row>
  </div>
</template>

<script>
import { filter as issueFilter, sort as issueSort } from '@/models/job/Issue'
import issueService from '@/services/issue'

import SearchTabNav from '@/components/common/SearchTabNav'

export default {
  components: {
    SearchTabNav,
  },
  data() {
    return {
      issueFilter,
      issueSort,
      issueSearchParams: {},
    }
  },
  async asyncData() {
    const issues = await issueService.searchIssues()
    return {
      issueList: issues.list,
      issueTotal: issues.total,
    }
  },
  methods: {
    async searchIssues(params) {
      const issues = await issueService.searchIssues(params)
      this.issueList = issues.list
      this.issueTotal = issues.total
      this.issueSearchParams = params
    },
    async issuePageChange(page) {
      const params = { page, ...this.issueSearchParams }
      const issues = await issueService.searchIssues(params)
      this.issueList = issues.list
    },
  },
}
</script>
