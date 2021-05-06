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
      <div
        v-if="type === 'tooltip'"
        class="column-user"
        :class="{ deleted: !scope.row[nameProp] }"
      >
        <el-tooltip :content="scope.row[nameProp] || $t('members.deletedUser')">
          <VirnectThumbnail
            :image="cdn(scope.row[imageProp])"
            :defaultImage="$defaultUserProfile"
          />
        </el-tooltip>
      </div>
      <!-- 일반 -->
      <div
        v-else
        class="column-user"
        :class="{ deleted: !scope.row[nameProp] }"
      >
        <el-tooltip :content="scope.row[nameProp] || $t('members.deletedUser')">
          <div>
            <VirnectThumbnail
              :image="cdn(scope.row[imageProp])"
              :defaultImage="$defaultUserProfile"
            />
            <span>{{ scope.row[nameProp] || $t('members.deletedUser') }}</span>
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
