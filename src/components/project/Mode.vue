<template>
  <div class="column-modes">
    <span v-for="mode in modeList" :key="mode" :class="modeClass(mode)">
      {{ $t(`${modeText(mode)}`) }}
    </span>
  </div>
</template>

<script>
import { modeFilter } from '@/models/project/Project'

export default {
  props: {
    modeList: Array,
  },
  methods: {
    // 모드의 value 값을 className에 맞게 변경합니다. ex) TWO_DIMENSINAL -> two-dimensinal
    modeClass(mode) {
      return mode.toLowerCase().replaceAll('_', '-')
    },
    // 모드의 label 값을 가져옵니다.
    modeText(mode) {
      return modeFilter.options.find(v => v.value == mode).label
    },
  },
}
</script>

<style lang="scss">
#projects .el-card--table .el-card__body .el-table .cell .column-modes {
  display: flex;
}
</style>
