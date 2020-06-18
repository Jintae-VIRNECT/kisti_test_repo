<template>
  <el-table-column
    :prop="prop"
    :label="label"
    :width="width"
    :sortable="sortable"
  >
    <template slot-scope="scope">
      <div class="column-plan">
        <img
          v-if="scope.row[productProp]"
          :src="products[scope.row[productProp].toLowerCase()].logo"
        />
        <span>{{ scope.row[prop] }}</span>
        <el-tag
          v-if="scope.row[gradeProp]"
          :class="scope.row[gradeProp]"
          effect="plain"
        >
          {{ scope.row[gradeProp] }}
        </el-tag>
      </div>
    </template>
  </el-table-column>
</template>

<script>
import products from '@/models/products'

export default {
  props: {
    prop: String,
    label: String,
    productProp: String,
    gradeProp: String,
    width: Number,
    sortable: [Boolean, String],
  },
  data() {
    return {
      products,
    }
  },
}
</script>
