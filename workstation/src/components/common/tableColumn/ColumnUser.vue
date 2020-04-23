<template>
  <el-table-column
    :prop="prop"
    :label="label"
    :width="width"
    :align="align"
    :sortable="sortable"
  >
    <template slot-scope="scope">
      <div class="column-user">
        <div class="avatar">
          <div
            class="image"
            :style="`background-image: url(${scope.row[imageProp]})`"
          />
        </div>
        <span>{{ scope.row[prop] }}</span>
      </div>
    </template>
  </el-table-column>
</template>

<script>
export default {
  props: {
    prop: String,
    imageProp: String,
    label: String,
    width: Number,
    align: String,
    sortable: [Boolean, String],
  },
}
</script>
