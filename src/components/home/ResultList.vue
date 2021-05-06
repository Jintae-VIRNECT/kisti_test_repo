<template>
  <el-card class="current-result-list el-card--table">
    <div slot="header">
      <h3>
        <span>{{ $t('home.reportList.title') }}</span>
      </h3>
    </div>
    <el-tabs v-model="activeTab">
      <el-tab-pane
        v-for="tab in tabs"
        :key="tab.name"
        :name="tab.name"
        :label="$t(tab.label)"
      >
      </el-tab-pane>
    </el-tabs>
    <!-- 작업 -->
    <el-table
      v-show="activeTab === 'task'"
      :data="subTasks"
      class="clickable"
      @row-click="moveToSubTask"
    >
      <ColumnDefault
        :label="$t('results.column.taskName')"
        prop="taskName"
        :width="180"
      />
      <ColumnDefault
        :label="$t('results.column.subTaskName')"
        prop="subTaskName"
      />
      <ColumnStatus
        :label="$t('results.column.status')"
        prop="conditions"
        :statusList="taskConditions"
        :width="120"
      />
      <ColumnUser
        :label="$t('results.column.reporter')"
        prop="workerUUID"
        nameProp="workerName"
        imageProp="workerProfile"
        type="tooltip"
        :width="80"
      />
      <ColumnDate
        :label="$t('results.column.reportedDate')"
        type="time"
        prop="reportedDate"
        :width="150"
      />
      <template slot="empty">
        <img src="~assets/images/empty/img-common-empty.jpg" />
        <p>{{ $t('home.reportList.empty') }}</p>
      </template>
    </el-table>
    <!-- 페이퍼 -->
    <el-table
      v-show="activeTab === 'paper'"
      :data="papers"
      class="clickable"
      @row-click="moveToPaper"
    >
      <ColumnDefault
        :label="$t('results.column.taskName')"
        prop="taskName"
        :width="180"
      />
      <ColumnDefault
        :label="$t('results.column.subTaskName')"
        prop="subTaskName"
        :width="180"
      />
      <ColumnDefault :label="$t('results.column.stepName')" prop="stepName" />
      <ColumnUser
        :label="$t('results.column.reporter')"
        prop="workerUUID"
        nameProp="workerName"
        imageProp="workerProfile"
        type="tooltip"
        :width="80"
      />
      <ColumnDate
        :label="$t('results.column.reportedDate')"
        type="time"
        prop="reportedDate"
        :width="150"
      />
      <template slot="empty">
        <img src="~assets/images/empty/img-common-empty.jpg" />
        <p>{{ $t('home.reportList.empty') }}</p>
      </template>
    </el-table>
    <!-- 이슈 -->
    <el-table
      v-show="activeTab === 'issue'"
      :data="issues"
      class="clickable"
      @row-click="moveToIssue"
    >
      <ColumnDefault
        :label="$t('results.column.taskName')"
        prop="taskName"
        :width="180"
      />
      <ColumnDefault
        :label="$t('results.column.subTaskName')"
        prop="subTaskName"
        :width="180"
      />
      <ColumnDefault
        :label="$t('results.column.stepName')"
        prop="stepName"
        :width="180"
      />
      <ColumnDefault :label="$t('results.column.issueTitle')" prop="caption" />
      <ColumnUser
        :label="$t('results.column.reporter')"
        prop="workerUUID"
        nameProp="workerName"
        imageProp="workerProfile"
        type="tooltip"
        :width="80"
      />
      <ColumnDate
        :label="$t('results.column.reportedDate')"
        type="time"
        prop="reportedDate"
        :width="150"
      />
      <template slot="empty">
        <img src="~assets/images/empty/img-common-empty.jpg" />
        <p>{{ $t('home.reportList.empty') }}</p>
      </template>
    </el-table>
  </el-card>
</template>

<script>
import resultService from '@/services/result'
import workspaceService from '@/services/workspace'
import columnMixin from '@/mixins/columns'
import { conditions as taskConditions } from '@/models/task/Task'

export default {
  mixins: [columnMixin],
  data() {
    return {
      activeTab: '',
      tabs: [
        {
          name: 'task',
          label: 'home.reportList.task',
        },
        {
          name: 'paper',
          label: 'home.reportList.paper',
        },
        {
          name: 'issue',
          label: 'home.reportList.issue',
        },
      ],
      contents: [],
      taskConditions,
      subTasks: [],
      papers: [],
      issues: [],
    }
  },
  watch: {
    async activeTab() {
      this.searchTabItems()
    },
  },
  methods: {
    moveToSubTask({ taskId, subTaskId }) {
      this.$router.push(`/tasks/${taskId}/${subTaskId}`)
    },
    moveToPaper({ taskId, subTaskId, stepId }) {
      this.$router.push(`/tasks/${taskId}/${subTaskId}/papers/${stepId}`)
    },
    moveToIssue({ taskId, subTaskId, stepId }) {
      this.$router.push(`/tasks/${taskId}/${subTaskId}/issues/${stepId}`)
    },
    async searchTabItems() {
      if (this.activeTab === 'task') {
        this.subTasks = (
          await resultService.searchCurrentSubTasks({ size: 4 })
        ).list
      }
      if (this.activeTab === 'paper') {
        this.papers = (await resultService.searchPapers({ size: 4 })).list
      }
      if (this.activeTab === 'issue') {
        this.issues = (await resultService.searchIssues({ size: 4 })).list
      }
    },
  },
  mounted() {
    this.activeTab = 'task'
    workspaceService.watchActiveWorkspace(this, this.searchTabItems)
  },
}
</script>

<style lang="scss">
.current-result-list {
  .el-tabs__nav-wrap {
    padding-left: 24px;
  }
  .el-tabs .el-tabs__item {
    height: 40px;
    font-size: 13px;
    line-height: 40px;
  }
  .el-table {
    margin-top: 6px;
  }
  .column-progress .el-progress-bar,
  .column-progress .el-progress__text {
    padding-right: 20px;
  }
}
</style>
