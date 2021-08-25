<template>
  <el-select
    class="member-seat-select"
    v-model="value"
    popper-class="member-seat-select__dropdown"
    filterable
    collapse-tags
    :placeholder="$t('members.setting.directInput')"
    :disabled="activeWorkspace.role === 'MEMBER' || !amount"
    @change="change"
  >
    <el-option
      v-for="(index, number) in amount"
      :key="index"
      :disabled="!amount"
      :value="number + 1"
    >
      <span>{{ number + 1 }}</span>
    </el-option>
  </el-select>
</template>

<script>
import { mapGetters } from 'vuex'
export default {
  props: {
    label: String,
    amount: Number,
    numOfSeat: Number,
  },
  computed: {
    ...mapGetters({
      activeWorkspace: 'auth/activeWorkspace',
    }),
  },
  watch: {
    numOfSeat(v) {
      this.value = v
    },
  },
  data() {
    return {
      value: '',
    }
  },
  methods: {
    change(v) {
      this.$emit('change', { name: this.label, amount: v })
    },
  },
}
</script>

<style lang="scss">
#__nuxt .seat-pane .el-form .el-input {
  width: 100%;
}
.member-seat-select {
  .el-select__tags {
    pointer-events: none;
  }
  .el-tag.el-tag--info {
    color: $font-color-desc;
    font-size: 13px;
    background: transparent;
    border: none;
    .el-tag__close {
      display: none;
    }
  }
  .el-input__inner {
    height: 40px;
  }
}
body .member-seat-select__dropdown {
  @media screen and (max-width: $mobile) {
    .el-select-dropdown__item {
      padding-right: 4px;
    }
  }
}
</style>
