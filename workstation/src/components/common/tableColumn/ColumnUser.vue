<template>
  <el-table-column
    :prop="prop"
    :label="label"
    :width="width"
    :align="align"
    :sortable="sortable"
  >
    <template slot-scope="scope">
      <!-- 툴팁 -->
      <div v-if="type === 'tooltip'" class="column-user">
        <el-tooltip :content="scope.row[nameProp]">
          <div class="avatar">
            <div
              class="image"
              :style="`background-image: url(${scope.row[imageProp]})`"
            />
          </div>
        </el-tooltip>
      </div>
      <!-- 일반 -->
      <div v-else class="column-user">
        <div class="avatar">
          <div
            class="image"
            :style="`background-image: url(${scope.row[imageProp]})`"
          />
        </div>
        <span>{{ scope.row[nameProp] }}</span>
      </div>
    </template>
  </el-table-column>
</template>

<script>
export default {
  props: {
    prop: String,
    type: String,
    nameProp: String,
    imageProp: String,
    label: String,
    width: Number,
    align: String,
    sortable: [Boolean, String],
  },
}
</script>
