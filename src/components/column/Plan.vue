<template>
  <el-table-column
    :prop="prop"
    :label="label"
    :width="width"
    :sortable="sortable"
  >
    <template slot-scope="scope">
      <div class="column-plan">
        <span>{{ scope.row[nameProp] }}</span>
        <el-tag :class="scope.row[gradeProp]" effect="plain">
          {{ scope.row[gradeProp] }}
        </el-tag>
      </div>
    </template>
  </el-table-column>
</template>

<script>
export default {
  props: {
    prop: String,
    label: String,
    nameProp: String,
    gradeProp: String,
    width: Number,
    sortable: [Boolean, String],
  },
}
</script>
