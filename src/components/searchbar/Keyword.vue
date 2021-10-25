<template>
  <div class="searchbar__keyword">
    <el-input
      v-model="myValue"
      :placeholder="$t(`searchbar.keyword.${placeholder}`)"
      @change="change"
    />
    <img src="~assets/images/icon/ic-search.svg" />
  </div>
</template>

<script>
export default {
  props: {
    value: String,
    options: Array,
    placeholder: {
      type: String,
      default: 'title',
    },
  },
  watch: {
    value(val) {
      this.myValue = val
    },
    myValue(val) {
      this.$emit('update:value', val)
    },
  },
  data() {
    return {
      myValue: this.value,
      holderText: this.$t(`searchbar.keyword.${this.placeholder}`),
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
.searchbar__keyword {
  position: relative;
  img {
    position: absolute;
    top: 0;
    bottom: 0;
    left: 8px;
    margin: auto 0;
  }
  .el-input__inner {
    padding-left: 30px;
    transition: border-color 0.3s, background 0.3s, width 0.2s ease;
  }
  .el-input:focus-within .el-input__inner {
    width: 200px;
    transition: border-color 0.3s, background 0.3s, width 0.2s ease;
  }
}
.searchbar__keyword__popper .el-select-dropdown__list {
  padding: 0;

  & > li {
    pointer-events: none;
  }
  & > li:first-child {
    .el-input {
      pointer-events: initial;
    }
  }
}
</style>
