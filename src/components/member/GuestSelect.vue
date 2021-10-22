<template>
  <el-select
    class="member-guest-select"
    v-model="value"
    popper-class="member-guest-select__dropdown"
    filterable
    collapse-tags
    :placeholder="$t('members.setting.directInput')"
    :disabled="activeWorkspace.role !== 'MASTER' || !amount"
    :no-match-text="$t('members.guest.noHavePlans')"
    @change="change"
  >
    <el-option
      v-for="(index, number) in amount"
      :key="index"
      :value="number + 1"
      :disabled="isMemberMaxium(number)"
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
    numOfGuest: String,
    membersTotal: Number,
    maximum: Number,
  },
  computed: {
    ...mapGetters({
      activeWorkspace: 'auth/activeWorkspace',
    }),
  },
  watch: {
    numOfGuest(v) {
      this.value = v
    },
  },
  data() {
    return {
      value: '',
    }
  },
  methods: {
    isMemberMaxium(number) {
      return this.maximum < this.membersTotal + (number + 1)
    },
    change(v) {
      this.$emit('change', { name: this.label, amount: v })
    },
  },
}
</script>

<style lang="scss">
#__nuxt .guest-pane .el-form .el-input {
  width: 100%;
}
.member-guest-select {
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
body .member-guest-select__dropdown {
  @media screen and (max-width: $mobile) {
    .el-select-dropdown__item {
      padding-right: 4px;
    }
  }
}
</style>
