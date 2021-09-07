<template>
  <el-table-column :prop="prop" :label="label" :width="width">
    <template slot-scope="scope">
      <div class="column-modes">
        <span
          v-for="mode in scope.row[prop]"
          :key="mode"
          :class="modeClass(mode)"
        >
          {{ $t(`${modeText(mode)}`) }}
        </span>
      </div>
    </template>
  </el-table-column>
</template>

<script>
import { modeFilter } from '@/models/project/Project'

export default {
  props: {
    prop: String,
    label: String,
    width: Number,
  },
  methods: {
    // 모드의 value 값을 className에 맞게 변경합니다. ex) TWO_DIMENSINAL -> two-dimensinal
    modeClass(mode) {
      return mode.toLowerCase().replaceAll('_', '-')
    },
    // 모드의 label 값을 가져옵니다.
    modeText(mode) {
      return modeFilter.options.filter(v => v.value == mode)[0].label
    },
  },
}
</script>

<style lang="scss">
#projects .el-card--table .el-card__body .el-table .cell .column-modes {
  display: flex;
}
</style>
