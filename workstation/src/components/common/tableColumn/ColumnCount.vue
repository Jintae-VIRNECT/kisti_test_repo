<template>
  <el-table-column :label="label" :width="width">
    <template slot-scope="scope">
      <div class="column-count">
        <div>
          <span>{{ scope.row[prop] }}</span>
          <span v-if="maxProp">/{{ scope.row[maxProp] }}</span>
        </div>
      </div>
    </template>
  </el-table-column>
</template>

<script>
export default {
  props: {
    label: String,
    prop: String,
    maxProp: String,
    width: Number,
  },
}
</script>
