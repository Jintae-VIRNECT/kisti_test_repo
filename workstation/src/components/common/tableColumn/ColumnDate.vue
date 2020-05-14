<template>
  <el-table-column
    :prop="prop"
    :label="label"
    :width="width"
    :align="align"
    :sortable="sortable"
  >
    <template slot-scope="scope">
      <div class="column-date">
        <span v-if="!scope.row[prop]">-</span>
        <span v-if="scope.row[prop]">
          {{ scope.row[prop] | localDateFormat }}
        </span>
        <span v-if="scope.row[prop2]">~</span>
        <span v-if="scope.row[prop2]">
          {{ scope.row[prop2] | localDateFormat }}
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
    prop: String,
    prop2: String,
    width: Number,
    align: String,
    sortable: [Boolean, String],
  },
  filters,
}
</script>
