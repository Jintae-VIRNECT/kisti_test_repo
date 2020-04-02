<template>
  <el-table-column :label="label" :width="width">
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
    label: String,
    nameProp: String,
    gradeProp: String,
    width: Number,
  },
}
</script>
