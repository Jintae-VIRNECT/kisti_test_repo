<template>
  <el-table-column :label="label" :width="width">
    <template slot-scope="scope">
      <div class="column-count">
        <span>{{ scope.row[prop] }}</span>
      </div>
    </template>
  </el-table-column>
</template>

<script>
export default {
  props: {
    label: String,
    prop: String,
    width: Number,
  },
}
</script>

<style lang="scss">
.column-count {
  & > span {
    display: inline-block;
    padding: 6px 10px;
    color: #114997;
    line-height: 1.1em;
    background-color: #fbfbfd;
    border: solid 1px #eaedf3;
    border-radius: 4px;
  }
}
</style>
