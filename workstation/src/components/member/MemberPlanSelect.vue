<template>
  <el-select
    class="member-plan-select"
    v-model="val"
    popper-class="member-plan-select__dropdown"
    multiple
  >
    <el-option :value="false" :label="$t('members.setting.givePlansEmpty')" />
    <el-option
      :value="true"
      :label="`${label} ${$t('common.plan')}`"
      :disabled="!amount"
    >
      <span>{{ `${label} ${$t('common.plan')}` }}</span>
      <span class="right">
        {{ amount }}
      </span>
    </el-option>
  </el-select>
</template>

<script>
export default {
  props: {
    value: String,
    label: String,
    amount: Number,
  },
  data() {
    return {
      val: [this.value],
    }
  },
  watch: {
    value(v) {
      this.val = [v]
    },
  },
  methods: {
    changed(v) {
      this.val = v.length ? [v[v.length - 1]] : [this.value]
      this.$emit('input', this.val[0])
    },
  },
}
</script>

<style lang="scss"></style>
