<template>
  <el-radio-group v-model="showMyList" @change="change">
    <el-radio-button :label="$t('common.all')" />
    <el-radio-button :label="mineLabel" />
  </el-radio-group>
</template>

<script>
export default {
  props: {
    mineLabel: String,
  },
  watch: {
    mineLabel(label) {
      if (this.showMyList !== this.$t('common.all')) {
        this.showMyList = label
      }
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
}
</script>
