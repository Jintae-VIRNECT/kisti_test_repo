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
          <el-button @click="showAll">
            {{ $t('common.all') }}
          </el-button>
          <el-button @click="showMine">
            {{ myResult }}
          </el-button>
          <span v-if="activeTab === 'task'">
            {{ $t('searchbar.filter.title') }}:
          </span>
          <searchbar-filter
            v-show="activeTab === 'task'"
            ref="filter"
            :value.sync="taskFilter.value"
            :options="taskFilter.options"
          />
        </el-col>
        <el-col class="right">
          <searchbar-keyword ref="keyword" :value.sync="resultsSearch" />
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
          <nuxt-child ref="table" :data="list" />
        </el-card>
      </el-row>
      <searchbar-page ref="page" :value.sync="page" :total="total" />
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
      taskFilter,
      resultsSearch: '',
      list: [],
      total: 0,
      page: 1,
      myResult: '',
    }
  },
  watch: {
    activeTab(tab) {
      if (tab === 'task') {
        this.$router.replace(`/tasks/results`)
        this.searchSubTasks()
        this.myResult = this.$t('results.myTask')
      } else if (tab === 'issue') {
        this.$router.replace(`/tasks/results/issues`)
        this.searchIssues()
        this.myResult = this.$t('results.myIssue')
      } else if (tab === 'paper') {
        this.$router.replace(`/tasks/results/papers`)
        this.searchPapers()
        this.myResult = this.$t('results.myPaper')
      }
    },
  },
  methods: {
    changedSearchParams(searchParams) {
      this.searchSubTasks(searchParams)
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
    showAll() {},
    showMine() {},
  },
  beforeMount() {
    const tab = this.$route.path.match(/[a-z]*?$/)[0]
    if (tab === 'results') this.activeTab = 'task'
    else if (tab === 'issues') this.activeTab = 'issue'
    else if (tab === 'papers') this.activeTab = 'paper'

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
