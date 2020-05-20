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

      <div>
        {{ taskInfo.name }}
      </div>

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

      <el-row>
        <el-card class="el-card--table">
          <div slot="header">
            <h3>{{ $t('task.list.allTasksList') }}</h3>
          </div>
          <el-table
            class="clickable"
            ref="table"
            :data="subTaskList"
            v-loading="loading"
          >
            <column-default
              :label="$t('task.detail.subTaskColumn.no')"
              prop="priority"
              :width="80"
            />
            <column-default
              :label="$t('task.detail.subTaskColumn.id')"
              prop="subTaskId"
              :width="140"
            />
            <column-default
              :label="$t('task.detail.subTaskColumn.name')"
              prop="subTaskName"
              sortable="custom"
            />
            <column-count
              :label="$t('task.detail.subTaskColumn.endedSteps')"
              prop="doneCount"
              maxProp="stepTotal"
              :width="120"
            />
            <column-date
              :label="$t('task.detail.subTaskColumn.schedule')"
              type="time"
              prop="startDate"
              prop2="endDate"
              :width="250"
            />
            <column-progress
              :label="$t('task.detail.subTaskColumn.progressRate')"
              prop="progressRate"
              :width="150"
            />
            <column-status
              :label="$t('task.detail.subTaskColumn.status')"
              prop="conditions"
              :statusList="taskConditions"
              :width="100"
            />
            <column-date
              :label="$t('task.detail.subTaskColumn.reportedDate')"
              type="time"
              prop="reportedDate"
              :width="130"
            />
            <column-boolean
              :label="$t('task.detail.subTaskColumn.issue')"
              prop="issuesTotal"
              :trueText="$t('task.list.hasIssue.yes')"
              :falseText="$t('task.list.hasIssue.no')"
              :width="80"
            />
            <column-default
              :label="$t('task.detail.subTaskColumn.endStatus')"
              prop="state"
              :width="100"
            />
          </el-table>
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

export default {
  mixins: [searchMixin, columnMixin],
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
