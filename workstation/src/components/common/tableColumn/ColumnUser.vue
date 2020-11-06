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
              :style="
                `background-image: url(${cdn(
                  scope.row[imageProp],
                )}), url(${$defaultUserProfile})`
              "
            />
          </div>
        </el-tooltip>
      </div>
      <!-- 일반 -->
      <div v-else class="column-user">
        <el-tooltip :content="scope.row[nameProp]">
          <div>
            <div class="avatar">
              <div
                class="image"
                :style="
                  `background-image: url('${cdn(
                    scope.row[imageProp],
                  )}'), url('${$defaultUserProfile}')`
                "
              />
            </div>
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
    label: String,
    width: Number,
    align: String,
    sortable: [Boolean, String],
  },
}
</script>
