<template>
  <el-table-column :label="label" :width="width">
    <template slot-scope="scope">
      <div class="column-progress">
        <el-progress :percentage="scope.row[prop]"></el-progress>
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
