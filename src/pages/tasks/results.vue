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
          <SearchbarKeyword
            ref="keyword"
            :value.sync="resultsSearch"
            @change="page = 1"
          />
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
      this.setActiveTab()
    },
  },
  methods: {
    /**
     * searchMixin에서 emitChangedSearchParams 실행시 changedSearchParams 사용
     */
    changedSearchParams() {
      this.setActiveTab()

      if (this.activeTab === 'task') this.searchSubTasks()
      else if (this.activeTab === 'issue') this.searchIssues()
      else if (this.activeTab === 'paper') this.searchPapers()
    },
    async searchSubTasks() {
      const { list, total } = await resultService.searchCurrentSubTasks(
        this.searchParams,
      )
      this.list.splice(0, this.list.length, ...list)
      this.total = total
    },
    async searchIssues() {
      const { list, total } = await resultService.searchIssues(
        this.searchParams,
      )
      this.list.splice(0, this.list.length, ...list)
      this.total = total
    },
    async searchPapers() {
      const { list, total } = await resultService.searchPapers(
        this.searchParams,
      )
      this.list.splice(0, this.list.length, ...list)
      this.total = total
    },
    sortChange({ prop, order }) {
      if (!order) this.emitChangedSearchParams()
      else {
        const sort = `${prop},${order.replace('ending', '')}`
        this.emitChangedSearchParams({ sort })
      }
    },
    /**
     * @author YongHo Kim <yhkim@virnect.com>
     * @description 라우터 이름을 가지고 activeTab 설정하는 함수
     */
    setActiveTab() {
      const { name } = this.$route
      if (name === 'tasks-results') this.activeTab = 'task'
      else if (name === 'tasks-results-issues') this.activeTab = 'issue'
      else if (name === 'tasks-results-papers') this.activeTab = 'paper'
    },
    /**
     * @description 데이터 조회 조건 초기화
     * @author YongHo Kim <yhkim@virnect.com>
     */
    refreshParams() {
      this.taskFilter.value = ['ALL']
      this.resultsSearch = this.$route.query.search // 특정 작업을 검색하기 위한 용도 인듯
      this.page = 1

      this.activeTab = 'task'
      this.searchParams.mine = false
    },
  },
  beforeMount() {
    // searchMixin.js: emitChangedSearchParams 실행 > 현재 페이지의 changedSearchParams 실행
    this.emitChangedSearchParams()
    workspaceService.watchActiveWorkspace(this, () => {
      this.refreshParams()
      this.emitChangedSearchParams()
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
