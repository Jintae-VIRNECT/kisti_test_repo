<template>
  <el-table-column
    :prop="prop"
    :label="label"
    :width="width"
    :align="align"
    :sortable="sortable"
  >
    <template slot-scope="scope">
      <div class="column-file-size">
        <span>
          {{ scope.row[prop] | byte2mb }}
        </span>
      </div>
    </template>
  </el-table-column>
</template>

<script>
import filters from '@/mixins/filters'

export default {
  mixins: [filters],
  props: {
    label: String,
    prop: String,
    width: Number,
    align: String,
    sortable: [Boolean, String],
  },
}
</script>
