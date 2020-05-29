<template>
  <el-card class="current-result-list el-card--table">
    <div slot="header">
      <h3>{{ $t('home.reportList.title') }}</h3>
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
      v-if="activeTab === 'task'"
      :data="subTasks"
      class="clickable"
      @row-click="moveToSubTask"
    >
      <column-default
        :label="$t('results.column.taskName')"
        prop="taskName"
        :width="180"
      />
      <column-default
        :label="$t('results.column.subTaskName')"
        prop="subTaskName"
      />
      <column-status
        :label="$t('results.column.status')"
        prop="conditions"
        :statusList="taskConditions"
        :width="100"
      />
      <column-user
        :label="$t('results.column.reporter')"
        prop="workerUUID"
        nameProp="workerName"
        imageProp="workerProfile"
        type="tooltip"
        :width="70"
      />
      <column-date
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
      v-if="activeTab === 'paper'"
      :data="papers"
      class="clickable"
      @row-click="moveToPaper"
    >
      <column-default
        :label="$t('results.column.taskName')"
        prop="taskName"
        :width="180"
      />
      <column-default
        :label="$t('results.column.subTaskName')"
        prop="subTaskName"
        :width="180"
      />
      <column-default :label="$t('results.column.stepName')" prop="stepName" />
      <column-user
        :label="$t('results.column.reporter')"
        prop="workerUUID"
        nameProp="workerName"
        imageProp="workerProfile"
        type="tooltip"
        :width="70"
      />
      <column-date
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
      v-if="activeTab === 'issue'"
      :data="issues"
      class="clickable"
      @row-click="moveToIssue"
    >
      <column-default
        :label="$t('results.column.taskName')"
        prop="taskName"
        :width="180"
      />
      <column-default
        :label="$t('results.column.subTaskName')"
        prop="subTaskName"
        :width="180"
      />
      <column-default
        :label="$t('results.column.stepName')"
        prop="stepName"
        :width="180"
      />
      <column-default :label="$t('results.column.issueTitle')" prop="caption" />
      <column-user
        :label="$t('results.column.reporter')"
        prop="workerUUID"
        nameProp="workerName"
        imageProp="workerProfile"
        type="tooltip"
        :width="70"
      />
      <column-date
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
    async activeTab(tab) {
      if (tab === 'task') {
        this.subTasks = (
          await resultService.searchCurrentSubTasks({ size: 4 })
        ).list
      }
      if (tab === 'paper') {
        this.papers = (await resultService.searchPapers({ size: 4 })).list
      }
      if (tab === 'issue') {
        this.issues = (await resultService.searchIssues({ size: 4 })).list
      }
    },
  },
  methods: {
    moveToSubTask({ taskId, subTaskId }) {
      this.$router.push(`/tasks/${taskId}/${subTaskId}`)
    },
    moveToPaper({ taskId, subTaskId, paperId }) {
      this.$router.push(`/tasks/${taskId}/${subTaskId}/papers/${paperId}`)
    },
    moveToIssue({ taskId, subTaskId, issueId }) {
      this.$router.push(`/tasks/${taskId}/${subTaskId}/issues/${issueId}`)
    },
  },
  mounted() {
    this.activeTab = 'task'
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
    font-size: 12.6px;
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
