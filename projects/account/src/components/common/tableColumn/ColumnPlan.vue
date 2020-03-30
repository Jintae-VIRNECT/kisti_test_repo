<template>
  <el-table-column :label="label" :width="width">
    <template slot-scope="scope">
      <div class="column-plan">
        <span>{{ scope.row[nameProp] }}</span>
        <el-tag :class="scope.row[gradeProp]" effect="plain">{{
          scope.row[gradeProp]
        }}</el-tag>
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

<style lang="scss">
.column-plan {
  & > .el-tag {
    height: auto;
    margin: 0 6px;
    padding: 4px 6px;
    font-size: 11px;
    line-height: 11px;

    &.PRO {
      color: #0b5bd8;
      border-color: #0b5bd8;
    }
  }
}
</style>
