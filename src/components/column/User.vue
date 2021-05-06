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
          <VirnectThumbnail
            :image="cdn(scope.row[imageProp])"
            :defaultImage="defaultImage"
          />
        </el-tooltip>
      </div>
      <!-- 툴팁없음 -->
      <div v-else-if="type === 'no-tooltip'" class="column-user">
        <VirnectThumbnail
          :image="cdn(scope.row[imageProp])"
          :defaultImage="defaultImage"
        />
        <span>{{ scope.row[nameProp] }}</span>
      </div>
      <!-- 일반 -->
      <div v-else class="column-user">
        <el-tooltip :content="scope.row[nameProp]">
          <div>
            <VirnectThumbnail
              :image="cdn(scope.row[imageProp])"
              :defaultImage="defaultImage"
            />
            <span>{{ scope.row[nameProp] }}</span>
          </div>
        </el-tooltip>
      </div>
    </template>
  </el-table-column>
</template>

<script>
import filterMixin from '@/mixins/filters'

export default {
  mixins: [filterMixin],
  props: {
    prop: String,
    type: String,
    nameProp: String,
    imageProp: String,
    defaultImage: String,
    label: String,
    width: Number,
    align: String,
    sortable: [Boolean, String],
  },
}
</script>
