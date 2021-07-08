<template>
  <el-radio-group v-model="showMyList" @change="change">
    <el-radio-button :label="$t('common.all')" />
    <el-radio-button :label="mineLabel" />
  </el-radio-group>
</template>

<script>
import workspaceService from '@/services/workspace'
export default {
  props: {
    mineLabel: String,
  },
  watch: {
    mineLabel(after, before) {
      this.showMyList =
        before === this.showMyList ? after : this.$t('common.all')
    },
  },
  data() {
    return {
      showMyList: this.$t('common.all'),
    }
  },
  methods: {
    change(label) {
      this.$emit('change', label)
    },
  },
  beforeMount() {
    // 워크스테이션 변경시 초기화
    workspaceService.watchActiveWorkspace(this, () => {
      this.showMyList = this.$t('common.all')
    })
  },
}
</script>
