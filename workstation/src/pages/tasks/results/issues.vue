<template>
  <el-table
    class="clickable"
    ref="table"
    :data="data"
    @row-click="rowClick"
    @sort-change="sortChange"
  >
    <column-default
      :label="$t('results.column.taskName')"
      prop="taskName"
      sortable="custom"
      :width="240"
    />
    <column-default
      :label="$t('results.column.subTaskName')"
      prop="subTaskName"
      sortable="custom"
      :width="240"
    />
    <column-default
      :label="$t('results.column.stepName')"
      prop="stepName"
      sortable="custom"
      :width="240"
    />
    <column-default
      :label="$t('results.column.issueTitle')"
      prop="caption"
      sortable="custom"
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
  </el-table>
</template>

<script>
import columnMixin from '@/mixins/columns'

export default {
  mixins: [columnMixin],
  props: {
    data: Array,
  },
  methods: {
    sortChange(params) {
      this.$emit('sort-change', params)
    },
    rowClick(row) {
      this.$router.push(
        `/tasks/${row.taskId}/${row.subTaskId}/issues/${row.issueId}`,
      )
    },
  },
}
</script>
