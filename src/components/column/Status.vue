<template>
  <el-table-column
    :prop="prop"
    :label="label"
    :width="width"
    :align="align"
    :sortable="sortable"
  >
    <template slot-scope="scope">
      <div class="column-status">
        <span :class="`color-bg-${code(scope.row[prop]).color}`">
          {{ $t(code(scope.row[prop]).label) }}
        </span>
      </div>
    </template>
  </el-table-column>
</template>

<script>
export default {
  props: {
    prop: String,
    label: String,
    /**
     * i18n object name
     */
    statusList: Array,
    width: Number,
    align: String,
    sortable: [Boolean, String],
  },
  methods: {
    code(code) {
      return this.statusList.find(status => status.value === code)
    },
  },
}
</script>
