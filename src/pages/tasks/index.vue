<template>
  <div id="tasks" class="task">
    <div class="container">
      <div class="title">
        <el-breadcrumb separator="/">
          <el-breadcrumb-item>{{ $t('menu.tasks') }}</el-breadcrumb-item>
          <el-breadcrumb-item>{{ $t('task.list.title') }}</el-breadcrumb-item>
        </el-breadcrumb>
        <h2>{{ $t('task.list.title') }}</h2>
        <el-button type="primary" @click="$router.push('/tasks/new')">
          {{ $t('task.list.newTask') }}
        </el-button>
      </div>

      <!-- 대시보드 -->
      <TaskDashboard :stat="taskStatistics" />

      <!-- 탭 -->
      <el-row class="tab-wrapper searchbar">
        <el-tabs v-model="activeTab">
          <el-tab-pane
            v-for="tab in tabs"
            :key="tab.name"
            :name="tab.name"
            :label="$t(tab.label)"
          />
        </el-tabs>
        <SearchbarKeyword ref="keyword" :value.sync="taskSearch" />
      </el-row>

      <!-- 버튼 영역 -->
      <el-row class="btn-wrapper searchbar">
        <el-col class="left">
          <SearchbarMine ref="mine" :mineLabel="$t('task.list.myTasks')" />
          <span>{{ $t('searchbar.filter.title') }}:</span>
          <SearchbarFilter
            ref="filter"
            :value.sync="taskFilter.value"
            :options="taskFilter.options"
            @change="filterChanged"
          />
        </el-col>
      </el-row>

      <el-row>
        <el-card class="el-card--table el-card--big">
          <!-- 테이블 -->
          <div slot="header" v-if="!isGraph">
            <h3>
              <span>{{ $t('task.list.allTasksList') }}</span>
            </h3>
            <el-button @click="isGraph = true">
              <img src="~assets/images/icon/ic-graph.svg" />
              <span>{{ $t('task.list.dailyRateGraph') }}</span>
            </el-button>
            <div class="right">
              <span>{{ $t('task.list.taskCount') }}</span>
              <span class="num">{{ taskTotal }}</span>
            </div>
          </div>
          <!-- 차트 -->
          <div slot="header" v-else>
            <h3>
              <span>{{ $t('task.list.dailyRateGraph') }}</span>
              <el-tooltip
                :content="$t('task.list.dailyRateGraphDesc')"
                placement="bottom-start"
              >
                <img src="~assets/images/icon/ic-error.svg" />
              </el-tooltip>
            </h3>
            <el-button @click="isGraph = false">
              <img src="~assets/images/icon/ic-list.svg" />
              <span>{{ $t('common.list') }}</span>
            </el-button>
          </div>

          <!-- 테이블 -->
          <TaskList
            v-show="!isGraph"
            ref="table"
            :data="taskList"
            :clickable="true"
            @updated="refreshTable"
            @deleted="refreshTable"
          />
          <!-- 차트 -->
          <TaskDailyGraph v-show="isGraph" />
        </el-card>
      </el-row>
      <SearchbarPage
        v-show="!isGraph"
        ref="page"
        :value.sync="taskPage"
        :total="taskTotal"
      />
    </div>
  </div>
</template>

<script>
import {
  conditions as taskConditions,
  filter as taskFilter,
  tabs,
} from '@/models/task/Task'
import searchMixin from '@/mixins/search'
import columnMixin from '@/mixins/columns'
import taskService from '@/services/task'
import workspaceService from '@/services/workspace'

export default {
  mixins: [searchMixin, columnMixin],
  async asyncData({ query }) {
    const taskSearch = query.search || null
    const promise = {
      tasks: taskService.searchTasks({ search: taskSearch }),
      stat: taskService.getTaskStatistics(),
    }
    return {
      taskList: (await promise.tasks).list,
      taskTotal: (await promise.tasks).total,
      taskStatistics: await promise.stat,
      taskSearch,
    }
  },
  data() {
    return {
      tabs,
      activeTab: 'allTasks',
      taskConditions,
      taskFilter: { ...taskFilter },
      taskPage: 1,
      taskTotal: 0,
      loading: false,
      isGraph: false,
    }
  },
  watch: {
    activeTab(tabName) {
      const enableFilter = tabs.find(tab => tab.name === tabName).filter
      this.taskFilter.value = tabName === 'allTasks' ? ['ALL'] : enableFilter
      this.taskFilter.options = taskFilter.options.map(option => ({
        ...option,
        disabled: !enableFilter.includes(option.value),
      }))
      this.emitChangedSearchParams()
    },
  },
  methods: {
    /**
     * searchMixin에서 emitChangedSearchParams 실행시 changedSearchParams 사용
     */
    async changedSearchParams() {
      this.searchTasks()
      this.taskStatistics = await taskService.getTaskStatistics()
    },
    async searchTasks() {
      const { list, total } = await taskService.searchTasks(this.searchParams)
      this.taskList = list
      this.taskTotal = total
    },
    filterChanged(filter) {
      if (!filter.length) this.activeTab = 'allTasks'
    },
    async refreshTable() {
      this.searchTasks()
      this.taskStatistics = await taskService.getTaskStatistics()
    },
    /**
     * @description 데이터 조회 조건 초기화
     * @author YongHo Kim <yhkim@virnect.com>
     */
    refreshParams() {
      // 검색 조건 초기화
      this.taskPage = 1 // 페이지
      this.activeTab = 'allTasks' // 탭
      this.taskFilter.value = ['ALL'] // 필터
      this.isGraph = false // 일자별 작업 진행률 그래프
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
