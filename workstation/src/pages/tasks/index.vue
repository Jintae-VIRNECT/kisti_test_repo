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
      <task-dashboard :stat="taskStatistics" />

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
        <searchbar-keyword ref="keyword" :value.sync="taskSearch" />
      </el-row>

      <!-- 버튼 영역 -->
      <el-row class="btn-wrapper searchbar">
        <el-col class="left">
          <el-button @click="showAll">
            {{ $t('common.all') }}
          </el-button>
          <el-button @click="showMine">
            {{ $t('task.list.myTasks') }}
          </el-button>
          <span>{{ $t('searchbar.filter.title') }}:</span>
          <searchbar-filter
            ref="filter"
            :value.sync="taskFilter.value"
            :options="taskFilter.options"
            @change="filterChanged"
          />
        </el-col>
      </el-row>

      <el-row>
        <el-card class="el-card--table el-card--big">
          <div slot="header">
            <h3>{{ $t('task.list.allTasksList') }}</h3>
            <div class="right">
              <span>{{ $t('task.list.taskCount') }}</span>
              <span class="num">{{ taskStatistics.totalTasks }}</span>
            </div>
          </div>
          <tasks-list
            ref="table"
            :data="taskList"
            :clickable="true"
            @updated="searchTasks"
            @deleted="searchTasks"
          />
        </el-card>
      </el-row>
      <searchbar-page ref="page" :value.sync="taskPage" :total="taskTotal" />
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
import TaskDashboard from '@/components/task/TaskDashboard'
import TasksList from '@/components/task/TasksList'

export default {
  mixins: [searchMixin, columnMixin],
  components: {
    TaskDashboard,
    TasksList,
  },
  async asyncData() {
    const promise = {
      tasks: taskService.searchTasks(),
      stat: taskService.getTaskStatistics(),
    }
    return {
      taskList: (await promise.tasks).list,
      taskTotal: (await promise.tasks).total,
      taskStatistics: await promise.stat,
    }
  },
  data() {
    return {
      tabs,
      activeTab: 'allTasks',
      taskConditions,
      taskFilter: { ...taskFilter },
      taskSearch: '',
      taskPage: 1,
      taskTotal: 0,
      loading: false,
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
    changedSearchParams(searchParams) {
      this.searchTasks(searchParams)
    },
    async searchTasks() {
      const { list, total } = await taskService.searchTasks(this.searchParams)
      this.taskList = list
      this.taskTotal = total
    },
    filterChanged(filter) {
      if (!filter.length) this.activeTab = 'allTasks'
    },
    showAll() {},
    showMine() {},
  },
  beforeMount() {
    workspaceService.watchActiveWorkspace(this, this.searchTasks)
  },
}
</script>
