<template>
  <el-table-column
    :prop="prop"
    :label="label"
    :width="width"
    :align="align"
    :sortable="sortable"
  >
    <template slot-scope="scope">
      <div class="column-count">
        <span>{{ scope.row[prop] }}{{ unit }}</span>
      </div>
    </template>
  </el-table-column>
</template>

<script>
export default {
  props: {
    prop: String,
    unit: String,
    label: String,
    width: Number,
    align: String,
    sortable: [Boolean, String],
  },
}
</script>
