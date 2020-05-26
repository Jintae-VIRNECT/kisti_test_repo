<template>
  <el-table
    :class="clickable ? 'clickable' : ''"
    ref="table"
    :data="data"
    @row-click="moveToTaskDetail"
  >
    <column-default :label="$t('task.list.column.id')" prop="id" :width="140" />
    <column-default
      :label="$t('task.list.column.name')"
      prop="name"
      sortable="custom"
    />
    <column-count
      :label="$t('task.list.column.endedSubTasks')"
      prop="doneCount"
      maxProp="subTaskTotal"
      :width="120"
    />
    <column-date
      :label="$t('task.list.column.schedule')"
      type="time"
      prop="startDate"
      prop2="endDate"
      :width="250"
    />
    <column-progress
      :label="$t('task.list.column.progressRate')"
      prop="progressRate"
      :width="150"
    />
    <column-status
      :label="$t('task.list.column.status')"
      prop="conditions"
      :statusList="taskConditions"
      :width="100"
    />
    <column-date
      :label="$t('task.list.column.reportedDate')"
      type="time"
      prop="reportedDate"
      :width="130"
    />
    <column-boolean
      :label="$t('task.list.column.issue')"
      prop="issuesTotal"
      :trueText="$t('task.list.hasIssue.yes')"
      :falseText="$t('task.list.hasIssue.no')"
      :width="80"
    />
    <column-default
      :label="$t('task.list.column.endStatus')"
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
  },
  data() {
    return {
      taskConditions,
    }
  },
  methods: {
    moveToTaskDetail({ id }) {
      if (!this.clickable) return false
      this.$router.push(`/tasks/${id}`)
    },
  },
}
</script>
