<template>
  <el-table
    :class="clickable ? 'clickable' : ''"
    ref="table"
    :data="data"
    @row-click="moveToSubTaskDetail"
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
</template>

<script>
import columnMixin from '@/mixins/columns'
import { conditions as taskConditions } from '@/models/task/Task'

export default {
  mixins: [columnMixin],
  props: {
    data: Array,
    clickable: Boolean,
    taskId: String,
  },
  data() {
    return {
      taskConditions,
    }
  },
  methods: {
    moveToSubTaskDetail({ subTaskId }) {
      if (!this.clickable) return false
      this.$router.push(`/tasks/${this.taskId}/${subTaskId}`)
    },
  },
}
</script>
