<template>
  <el-table-column
    :prop="prop"
    :label="label"
    :width="width"
    :align="align"
    :sortable="sortable"
  >
    <template slot-scope="scope">
      <div class="column-price">
        <span>
          {{ toPriceString(scope.row[prop]) }}
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
    prop: String,
    label: String,
    width: Number,
    align: String,
    sortable: [Boolean, String],
  },
}
</script>
