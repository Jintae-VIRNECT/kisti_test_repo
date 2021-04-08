<template>
  <el-table-column
    :prop="prop"
    :label="label"
    :width="width"
    :align="align"
    :sortable="sortable"
  >
    <template slot-scope="scope">
      <div class="column-default">
        <span v-if="customFilter">{{ filter(scope.row[prop]) }}</span>
        <span v-else>{{ scope.row[prop] }}</span>
      </div>
    </template>
  </el-table-column>
</template>

<script>
import filterMixin from '@/mixins/filters'

export default {
  mixins: [filterMixin],
  props: {
    prop: String,
    label: String,
    width: Number,
    align: String,
    customFilter: String,
    sortable: [Boolean, String],
  },
  methods: {
    filter(val) {
      return this[this.customFilter](val)
    },
  },
}
</script>
