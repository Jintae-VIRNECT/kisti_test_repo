<template>
  <el-table-column :label="label" :width="width" :align="align">
    <template slot-scope="scope">
      <div class="column-default">
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
    align: String,
  },
}
</script>
