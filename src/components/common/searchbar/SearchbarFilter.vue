<template>
  <div class="searchbar__filter">
    <el-select
      v-bind:class="filterSelected"
      v-model="myValue"
      @change="change"
      multiple
      collapse-tags
    >
      <el-option
        v-for="option in options"
        :key="option.value"
        :value="option.value"
        :label="$t(option.label)"
        :disabled="option.disabled"
      />
    </el-select>
  </div>
</template>

<script>
export default {
  props: {
    value: Array,
    options: Array,
  },
  watch: {
    value(val) {
      this.myValue = val
    },
    myValue(val) {
      this.$emit('update:value', val)
    },
  },
  computed: {
    filterSelected() {
      return this.myValue[0].toLowerCase() !== 'all' ? 'selected' : 'empty'
    },
  },
  data() {
    return {
      myValue: this.value,
    }
  },
  methods: {
    change() {
      this.$emit('change', this.myValue)
    },
  },
}
</script>

<style lang="scss">
#__nuxt .searchbar .searchbar__filter {
  .el-select .el-tag {
    background: none;
    border: none;
    .el-icon-close {
      display: none;
    }
  }
  .selected {
    .el-input__inner {
      background: $color-selected;
      border-color: $color-selected;
    }
    .el-tag {
      padding-right: 4px;
      color: #fff;
    }
    .el-tag:nth-child(2) {
      margin: 0;
      padding: 0;
      letter-spacing: -0.1em;
    }
  }
}
</style>
