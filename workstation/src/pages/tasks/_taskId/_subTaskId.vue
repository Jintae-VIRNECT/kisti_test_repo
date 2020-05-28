<template>
  <div id="sub-task-detail" class="task">
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
          <sub-tasks-list :data="[subTaskInfo]" @updated="subTaskUpdated" />
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
            <h3>{{ $t('task.subTaskDetail.stepsList') }}</h3>
          </div>
          <el-table ref="table" :data="stepsList" v-loading="loading">
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
            <el-table-column
              prop="issue"
              :label="$t('task.subTaskDetail.stepsColumn.issue')"
              :width="90"
            >
              <template slot-scope="scope">
                <el-button
                  v-if="scope.row.issue"
                  @click="moveToIssue(scope.row.issue.id)"
                >
                  {{ $t('task.subTaskDetail.showIssue') }}
                </el-button>
              </template>
            </el-table-column>
            <el-table-column
              prop="paper"
              :label="$t('task.subTaskDetail.stepsColumn.paper')"
              :width="130"
            >
              <template slot-scope="scope">
                <el-button
                  v-if="scope.row.paper"
                  @click="moveToPaper(scope.row.paper.id)"
                >
                  {{ $t('task.subTaskDetail.showPaper') }}
                </el-button>
              </template>
            </el-table-column>
          </el-table>
        </el-card>
      </el-row>
      <searchbar-page ref="page" :value.sync="stepsPage" :total="stepsTotal" />
    </div>
    <nuxt-child />
  </div>
</template>

<script>
import {
  conditions as taskConditions,
  filter as taskFilter,
} from '@/models/task/Task'
import { tabs } from '@/models/task/Step'
import searchMixin from '@/mixins/search'
import columnMixin from '@/mixins/columns'

import workspaceService from '@/services/workspace'
import taskService from '@/services/task'
import SubTasksList from '@/components/task/SubTasksList'

export default {
  mixins: [searchMixin, columnMixin],
  components: {
    SubTasksList,
  },
  async asyncData({ params }) {
    const promise = {
      subTaskDetail: taskService.getSubTaskDetail(params.subTaskId),
      steps: taskService.searchSteps(params.subTaskId),
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
    async subTaskUpdated() {
      this.subTaskInfo = await taskService.getSubTaskDetail(
        this.subTaskInfo.subTaskId,
      )
    },
    changedSearchParams(searchParams) {
      this.searchSteps(searchParams)
    },
    async searchSteps() {
      const { list, total } = await taskService.searchSteps(
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
    moveToIssue(issueId) {
      this.$router.replace(
        `${this.$router.currentRoute.path}/issues/${issueId}`,
      )
    },
    moveToPaper(paperId) {
      this.$router.replace(
        `${this.$router.currentRoute.path}/papers/${paperId}`,
      )
    },
  },
  beforeMount() {
    workspaceService.watchActiveWorkspace(this, () => {
      this.$router.replace('/tasks')
    })
  },
}
</script>

<style lang="scss">
#__nuxt #sub-task-detail {
  .cell .el-button {
    padding: 8px 11px;
    color: #0d2a58;
    font-size: 12.6px;
  }
}
</style>
