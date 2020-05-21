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
          {{ format(scope.row[prop]) }}
        </span>
        <span v-if="scope.row[prop2]">-</span>
        <span v-if="scope.row[prop2]">
          {{ format(scope.row[prop2]) }}
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
    type: String,
    prop: String,
    prop2: String,
    width: Number,
    align: String,
    sortable: [Boolean, String],
  },
  methods: {
    format(date) {
      if (!this.type) return filters.localDateFormat(date)
      if (this.type === 'date') return filters.localDateFormat(date)
      if (this.type === 'time') return filters.localTimeFormat(date)
    },
  },
}
</script>
