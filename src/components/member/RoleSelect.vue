<template>
  <el-select
    ref="roleSelect"
    class="member-role-select"
    :class="[val[0], disabled ? 'disabled' : null]"
    v-model="val"
    multiple
    @change="changed"
    popper-class="member-role-select__dropdown"
    :disabled="disabled"
  >
    <el-option
      class="column-role"
      v-for="role in roles"
      :key="role.value"
      :value="role.value"
    >
      <el-tag :class="role.value">{{ $t(role.label) }}</el-tag>
    </el-option>
  </el-select>
</template>

<script>
import { role } from '@/models/workspace/Member'

export default {
  props: {
    value: String,
    disabled: Boolean,
  },
  data() {
    return {
      roles: role.options.filter(
        ({ value }) => value !== 'MASTER' && value !== 'GUEST',
      ),
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
      this.$refs.roleSelect.blur()
    },
  },
}
</script>

<style lang="scss">
#__nuxt .member-role-select {
  cursor: pointer;
  &.disabled {
    cursor: not-allowed;
  }

  .el-tag__close {
    display: none;
  }
  .el-select__tags {
    height: 38px;
    overflow: hidden;
    .el-tag {
      margin: 7px 8px;
    }
  }

  &.MASTER .el-tag {
    background: #365595;
  }
  &.MANAGER .el-tag {
    background: #1f87e6;
  }
  &.MEMBER .el-tag {
    background: #7ac1fc;
  }
}
body .el-popper.el-select-dropdown.is-multiple.member-role-select__dropdown {
  .el-select-dropdown__item::before,
  .el-select-dropdown__item::after {
    display: none;
  }
  .el-select-dropdown__list {
    padding: 6px 0;
  }
  .el-select-dropdown__item.hover {
    background: #e9ecf1;
  }
  .el-select-dropdown__item {
    padding-left: 11px;
  }
}
</style>
