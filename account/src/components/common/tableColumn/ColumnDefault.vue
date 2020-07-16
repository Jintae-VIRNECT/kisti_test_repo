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
        <span v-if="dangerouslyUseHTMLString" v-html="scope.row[prop]" />
        <span v-else>{{ scope.row[prop] }}</span>
      </div>
    </template>
  </el-table-column>
</template>

<script>
export default {
  props: {
    prop: String,
    label: String,
    dangerouslyUseHTMLString: Boolean,
    width: Number,
    align: String,
    sortable: [Boolean, String],
  },
}
</script>
