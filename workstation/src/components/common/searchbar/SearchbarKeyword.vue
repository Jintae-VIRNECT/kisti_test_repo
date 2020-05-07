<template>
  <div class="searchbar__keyword">
    <el-input
      v-model="myValue"
      :placeholder="$t('searchbar.keyword.title')"
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
    text-indent: 18px;
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
