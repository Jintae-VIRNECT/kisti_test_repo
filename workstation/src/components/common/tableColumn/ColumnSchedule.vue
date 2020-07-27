<template>
  <el-table-column :label="label" :width="width">
    <template slot-scope="scope">
      <div class="column-schedule">
        <span>
          {{ scope.row[startProp] | localTimeFormat }}
          ~
          {{ scope.row[endProp] | localTimeFormat }}
        </span>
      </div>
    </template>
  </el-table-column>
</template>

<script>
import { filters } from '@/plugins/dayjs'
export default {
  props: {
    label: String,
    startProp: String,
    endProp: String,
    width: Number,
  },
  filters,
}
</script>
