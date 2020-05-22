<template>
  <el-table class="clickable" ref="table" :data="data">
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
    <column-date
      :label="$t('task.detail.subTaskColumn.reportedDate')"
      type="time"
      prop="reportedDate"
      :width="130"
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
}
</script>
