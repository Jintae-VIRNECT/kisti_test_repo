<template>
  <div id="task-detail" class="task">
    <div class="container">
      <div class="title">
        <el-breadcrumb separator="/">
          <el-breadcrumb-item>{{ $t('menu.tasks') }}</el-breadcrumb-item>
          <el-breadcrumb-item to="/tasks">{{
            $t('task.list.title')
          }}</el-breadcrumb-item>
          <el-breadcrumb-item>{{ $t('task.detail.title') }}</el-breadcrumb-item>
        </el-breadcrumb>
        <h2>{{ $t('task.detail.title') }}</h2>
        <el-button type="primary" @click="$router.push('/tasks/new')">
          {{ $t('task.list.newTask') }}
        </el-button>
      </div>

      <!-- 작업 정보 -->
      <el-row>
        <el-card class="el-card--table el-card--table--info">
          <div slot="header">
            <router-link to="/tasks">
              <img src="~assets/images/icon/ic-arrow-back.svg" />
            </router-link>
            <h3>{{ $t('task.detail.title') }}</h3>
            <div class="right">
              <span>
                {{ $t('task.detail.taskPosition') }} : {{ taskInfo.position }}
              </span>
            </div>
          </div>
          <tasks-list
            :data="[taskInfo]"
            @updated="taskUpdated"
            @deleted="$router.push('/tasks')"
          />
        </el-card>
      </el-row>

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
        <searchbar-keyword ref="keyword" :value.sync="subTaskSearch" />
      </el-row>

      <!-- 버튼 영역 -->
      <el-row class="btn-wrapper searchbar">
        <el-col class="left">
          <el-button @click="showAll">
            {{ $t('common.all') }}
          </el-button>
          <el-button @click="showMine">
            {{ $t('task.detail.mySubTasks') }}
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

      <!-- 하위 작업 -->
      <el-row>
        <el-card class="el-card--table el-card--big">
          <!-- 테이블 -->
          <div slot="header" v-if="!isGraph">
            <h3>{{ $t('task.detail.subTaskList') }}</h3>
            <el-button @click="isGraph = true">
              <img src="~assets/images/icon/ic-graph.svg" />
              <span>{{ $t('common.graph') }}</span>
            </el-button>
            <div class="right">
              <span>{{ $t('task.detail.subTaskCount') }}</span>
              <span class="num">{{ subTaskTotal }}</span>
            </div>
          </div>
          <!-- 차트 -->
          <div slot="header" v-else>
            <h3>{{ $t('task.detail.subTasksProgressGraph') }}</h3>
            <el-button @click="isGraph = false">
              <img src="~assets/images/icon/ic-list.svg" />
              <span>{{ $t('common.list') }}</span>
            </el-button>
            <div class="right">
              <span>{{ $t('task.detail.subTaskCount') }}</span>
              <span class="num">{{ subTaskTotal }}</span>
            </div>
          </div>

          <!-- 테이블 -->
          <sub-tasks-list
            v-if="!isGraph"
            ref="table"
            :data="subTaskList"
            :taskInfo="taskInfo"
            :clickable="true"
            @updated="searchSubTasks"
          />
          <!-- 차트 -->
          <tasks-list-graph v-else :data="subTaskList" type="subTask" />
        </el-card>
      </el-row>
      <searchbar-page
        ref="page"
        :value.sync="subTaskPage"
        :total="subTaskTotal"
      />
    </div>
  </div>
</template>

<script>
import {
  conditions as taskConditions,
  filter as taskFilter,
} from '@/models/task/Task'
import { tabs } from '@/models/task/SubTask'
import searchMixin from '@/mixins/search'
import columnMixin from '@/mixins/columns'

import workspaceService from '@/services/workspace'
import taskService from '@/services/task'
import TasksList from '@/components/task/TasksList'
import SubTasksList from '@/components/task/SubTasksList'
import TasksListGraph from '@/components/task/TasksListGraph'

export default {
  mixins: [searchMixin, columnMixin],
  components: {
    TasksList,
    SubTasksList,
    TasksListGraph,
  },
  async asyncData({ params }) {
    const promise = {
      taskDetail: taskService.getTaskDetail(params.taskId),
      subTask: taskService.searchSubTasks(params.taskId),
    }
    return {
      taskInfo: await promise.taskDetail,
      subTaskList: (await promise.subTask).list,
      subTaskTotal: (await promise.subTask).total,
    }
  },
  data() {
    return {
      tabs,
      activeTab: 'allSubTasks',
      taskConditions,
      taskFilter: { ...taskFilter },
      subTaskSearch: '',
      subTaskPage: 1,
      loading: false,
      isGraph: false,
    }
  },
  watch: {
    activeTab(tabName) {
      const enableFilter = tabs.find(tab => tab.name === tabName).filter
      this.taskFilter.value = tabName === 'allSubTasks' ? ['ALL'] : enableFilter
      this.taskFilter.options = taskFilter.options.map(option => ({
        ...option,
        disabled: !enableFilter.includes(option.value),
      }))
      this.emitChangedSearchParams()
    },
  },
  methods: {
    async taskUpdated() {
      this.taskInfo = await taskService.getTaskDetail(this.taskInfo.id)
    },
    changedSearchParams(searchParams) {
      this.searchSubTasks(searchParams)
    },
    async searchSubTasks() {
      const { list, total } = await taskService.searchSubTasks(
        this.taskInfo.id,
        this.searchParams,
      )
      this.subTaskList = list
      this.subTaskTotal = total
    },
    filterChanged(filter) {
      if (!filter.length) this.activeTab = 'allSubTasks'
    },
    showAll() {},
    showMine() {},
  },
  beforeMount() {
    workspaceService.watchActiveWorkspace(this, () => {
      this.$router.replace('/tasks')
    })
  },
}
</script>
