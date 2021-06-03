<template>
  <div id="results">
    <div class="container">
      <div class="title">
        <el-breadcrumb separator="/">
          <el-breadcrumb-item>{{ $t('menu.tasks') }}</el-breadcrumb-item>
          <el-breadcrumb-item>{{ $t('results.title') }}</el-breadcrumb-item>
        </el-breadcrumb>
        <h2>{{ $t('results.title') }}</h2>
      </div>
      <!-- 검색 영역 -->
      <el-row class="searchbar">
        <el-col class="left">
          <SearchbarMine ref="mine" :mineLabel="myResult" />
          <span v-if="activeTab === 'task'">
            {{ $t('searchbar.filter.title') }}:
          </span>
          <SearchbarFilter
            v-show="activeTab === 'task'"
            ref="filter"
            :value.sync="taskFilter.value"
            :options="taskFilter.options"
          />
        </el-col>
        <el-col class="right">
          <SearchbarKeyword ref="keyword" :value.sync="resultsSearch" />
        </el-col>
      </el-row>
      <!-- 테이블 -->
      <el-row>
        <el-card class="el-card--table">
          <el-tabs slot="header" v-model="activeTab">
            <el-tab-pane
              v-for="tab in tabs"
              :key="tab.name"
              :name="tab.name"
              :label="$t(tab.label)"
            >
            </el-tab-pane>
          </el-tabs>
          <nuxt-child :data="list" @sort-change="sortChange" />
        </el-card>
      </el-row>
      <SearchbarPage ref="page" :value.sync="page" :total="total" />
    </div>
  </div>
</template>

<script>
import search from '@/mixins/search'
import resultService from '@/services/result'
import workspaceService from '@/services/workspace'
import { filter as taskFilter } from '@/models/task/Task'

export default {
  mixins: [search],
  data() {
    return {
      activeTab: '',
      tabs: [
        {
          name: 'task',
          label: 'results.task',
        },
        {
          name: 'paper',
          label: 'results.paper',
        },
        {
          name: 'issue',
          label: 'results.issue',
        },
      ],
      taskFilter: { ...taskFilter },
      resultsSearch: '',
      list: [],
      total: 0,
      page: 1,
      myResult: '',
    }
  },
  watch: {
    activeTab(tab) {
      let pathTo = ''
      if (tab === 'task') {
        pathTo = '/tasks/results'
        this.searchSubTasks()
        this.myResult = this.$t('results.myTask')
      } else if (tab === 'issue') {
        pathTo = '/tasks/results/issues'
        this.searchIssues()
        this.myResult = this.$t('results.myIssue')
      } else if (tab === 'paper') {
        pathTo = '/tasks/results/papers'
        this.searchPapers()
        this.myResult = this.$t('results.myPaper')
      }
      this.$router.replace(pathTo).catch(() => {})
    },
    $route() {
      const { path } = this.$route
      if (path === '/tasks/results') this.activeTab = 'task'
      else if (path === '/tasks/results/issues') this.activeTab = 'issue'
      else if (path === '/tasks/results/papers') this.activeTab = 'paper'
    },
  },
  methods: {
    changedSearchParams() {
      if (this.activeTab === 'task') this.searchSubTasks()
      else if (this.activeTab === 'issue') this.searchIssues()
      else if (this.activeTab === 'paper') this.searchPapers()
    },
    async searchSubTasks() {
      const { list, total } = await resultService.searchCurrentSubTasks(
        this.searchParams,
      )
      this.list = list
      this.total = total
    },
    async searchIssues() {
      const { list, total } = await resultService.searchIssues(
        this.searchParams,
      )
      this.list = list
      this.total = total
    },
    async searchPapers() {
      const { list, total } = await resultService.searchPapers(
        this.searchParams,
      )
      this.list = list
      this.total = total
    },
    sortChange({ prop, order }) {
      if (!order) this.emitChangedSearchParams()
      else {
        const sort = `${prop},${order.replace('ending', '')}`
        this.emitChangedSearchParams({ sort })
      }
    },
  },
  beforeMount() {
    this.resultsSearch = this.$route.query.search
    this.searchParams.search = this.$route.query.search

    const { path } = this.$route
    if (path === '/tasks/results') this.activeTab = 'task'
    else if (path === '/tasks/results/issues') this.activeTab = 'issue'
    else if (path === '/tasks/results/papers') this.activeTab = 'paper'

    workspaceService.watchActiveWorkspace(this, () => {
      if (this.activeTab === 'task') this.searchSubTasks()
      else if (this.activeTab === 'issue') this.searchIssues()
      else if (this.activeTab === 'paper') this.searchPapers()
    })
  },
}
</script>

<style lang="scss">
#results {
  .el-card__header {
    padding-top: 0;
    padding-bottom: 0;

    .el-tabs .el-tabs__item {
      height: 56px;
      line-height: 56px;
    }
  }
}
</style>
