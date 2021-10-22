<template>
  <el-select
    class="member-plan-select"
    v-model="myValue"
    popper-class="member-plan-select__dropdown"
    multiple
    collapse-tags
    :disabled="activeWorkspace.role !== 'MASTER' || isGuest"
    @change="change"
  >
    <el-option :value="false" :label="$t('members.setting.givePlansEmpty')" />
    <el-option
      :value="true"
      :label="`${label} ${$t('common.plan')}`"
      :disabled="!amount"
    >
      <span>{{ `${label} ${$t('common.plan')}` }}</span>
      <span class="right" :class="amount ? '' : 'empty'">
        {{ amount }}
      </span>
    </el-option>
  </el-select>
</template>

<script>
import { mapGetters } from 'vuex'
export default {
  props: {
    value: Boolean,
    label: String,
    amount: Number,
    role: String,
    isGuest: {
      type: Boolean,
      default: false,
    },
  },
  computed: {
    ...mapGetters({
      activeWorkspace: 'auth/activeWorkspace',
    }),
  },
  data() {
    return {
      myValue: [this.value],
    }
  },
  watch: {
    value(v) {
      this.myValue = [v]
    },
    myValue(v) {
      this.$emit('input', v[0])
      this.$emit('change', this.myValue[0])
    },
  },
  methods: {
    change() {
      this.myValue = [this.myValue[this.myValue.length - 1]]
      if (!this.myValue[0]) this.myValue = [false]
    },
  },
}
</script>

<style lang="scss">
.member-plan-select {
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
}
body .member-plan-select__dropdown {
  span.right {
    margin-top: 9px;
    margin-left: 2px;
    padding: 0 8px;
    color: rgba(255, 255, 255, 0.9);
    font-weight: bold;
    line-height: 1.5;
    background: $color-primary;
    border-radius: 10px;

    &.empty {
      filter: grayscale(1);
    }
  }

  @media screen and (max-width: $mobile) {
    .el-select-dropdown__item {
      padding-right: 4px;
    }
  }
}
</style>
