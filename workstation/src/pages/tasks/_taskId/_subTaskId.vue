<template>
  <div id="task-detail" class="task">
    <div class="container">
      <div class="title">
        <el-breadcrumb separator="/">
          <el-breadcrumb-item>{{ $t('menu.tasks') }}</el-breadcrumb-item>
          <el-breadcrumb-item to="/tasks">{{
            $t('task.list.title')
          }}</el-breadcrumb-item>
          <el-breadcrumb-item :to="`/tasks/${$route.params.taskId}`">{{
            $t('task.detail.title')
          }}</el-breadcrumb-item>
          <el-breadcrumb-item>{{
            $t('task.subTaskDetail.title')
          }}</el-breadcrumb-item>
        </el-breadcrumb>
        <h2>{{ $t('task.subTaskDetail.title') }}</h2>
        <el-button type="primary" @click="$router.push('/tasks/new')">
          {{ $t('task.list.newTask') }}
        </el-button>
      </div>

      <!-- 하위 작업 정보 -->
      <el-row>
        <el-card class="el-card--table el-card--table--info">
          <div slot="header">
            <h3>{{ $t('task.subTaskDetail.title') }}</h3>
          </div>
          <el-table :data="[subTaskInfo]" v-loading="loading">
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
        <searchbar-keyword ref="keyword" :value.sync="stepsSearch" />
      </el-row>

      <!-- 버튼 영역 -->
      <el-row class="btn-wrapper searchbar">
        <el-col class="left">
          <el-button @click="showAll">
            {{ $t('common.all') }}
          </el-button>
          <el-button @click="showMine">
            {{ $t('task.subTaskDetail.mySteps') }}
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

      <!-- 단계 리스트 -->
      <el-row>
        <el-card class="el-card--table">
          <div slot="header">
            <h3>{{ $t('task.list.allTasksList') }}</h3>
          </div>
          <el-table
            class="clickable"
            ref="table"
            :data="stepsList"
            v-loading="loading"
          >
            <column-default
              :label="$t('task.subTaskDetail.stepsColumn.no')"
              prop="priority"
              :width="80"
            />
            <column-default
              :label="$t('task.subTaskDetail.stepsColumn.id')"
              prop="id"
              :width="140"
            />
            <column-default
              :label="$t('task.subTaskDetail.stepsColumn.name')"
              prop="name"
              sortable="custom"
            />
            <column-count
              :label="$t('task.subTaskDetail.stepsColumn.endedActions')"
              prop="doneCount"
              maxProp="actionTotal"
              :width="120"
            />
            <column-progress
              :label="$t('task.subTaskDetail.stepsColumn.progressRate')"
              prop="progressRate"
              :width="150"
            />
            <column-status
              :label="$t('task.subTaskDetail.stepsColumn.status')"
              prop="conditions"
              :statusList="taskConditions"
              :width="100"
            />
            <column-date
              :label="$t('task.subTaskDetail.stepsColumn.reportedDate')"
              type="time"
              prop="reportedDate"
              :width="130"
            />
            <column-default
              :label="$t('task.subTaskDetail.stepsColumn.issue')"
              prop="issue"
              :width="90"
            />
            <column-default
              :label="$t('task.subTaskDetail.stepsColumn.paper')"
              prop="paper"
              :width="110"
            />
          </el-table>
        </el-card>
      </el-row>
      <searchbar-page ref="page" :value.sync="stepsPage" :total="stepsTotal" />
    </div>
  </div>
</template>

<script>
import {
  conditions as taskConditions,
  filter as taskFilter,
} from '@/models/task/Task'
import { tabs } from '@/models/step/Step'
import searchMixin from '@/mixins/search'
import columnMixin from '@/mixins/columns'

import workspaceService from '@/services/workspace'
import taskService from '@/services/task'
import stepService from '@/services/step'

export default {
  mixins: [searchMixin, columnMixin],
  async asyncData({ params }) {
    const promise = {
      subTaskDetail: taskService.getSubTaskDetail(params.subTaskId),
      steps: stepService.searchSteps(params.subTaskId),
    }
    return {
      subTaskInfo: await promise.subTaskDetail,
      stepsList: (await promise.steps).list,
      stepsTotal: (await promise.steps).total,
    }
  },
  data() {
    return {
      tabs,
      activeTab: 'allSteps',
      taskConditions,
      taskFilter: { ...taskFilter },
      stepsSearch: '',
      stepsPage: 1,
      loading: false,
    }
  },
  watch: {
    activeTab(tabName) {
      const enableFilter = tabs.find(tab => tab.name === tabName).filter
      this.taskFilter.value = tabName === 'allSteps' ? ['ALL'] : enableFilter
      this.taskFilter.options = taskFilter.options.map(option => ({
        ...option,
        disabled: !enableFilter.includes(option.value),
      }))
      this.emitChangedSearchParams()
    },
  },
  methods: {
    changedSearchParams(searchParams) {
      this.searchSteps(searchParams)
    },
    async searchSteps() {
      const { list, total } = await stepService.searchSteps(
        this.subTaskInfo.subTaskId,
        this.searchParams,
      )
      this.stepsList = list
      this.stepsTotal = total
    },
    filterChanged(filter) {
      if (!filter.length) this.activeTab = 'allSteps'
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
