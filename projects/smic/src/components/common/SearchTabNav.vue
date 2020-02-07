<template lang="pug">
  div
    el-input.tool.search(:placeholder='placeholder' v-model='searchInput' @change="onChangeSearch()")
      el-button(slot='append' icon='el-icon-search')
    span 필터 : 
    el-select.filter(v-model='filterValue' multiple placeholder='Select' @change="onFilterChange(filterValue)" v-bind:class="filterSelected")
      el-option(v-for='item in filter.options' :key='item.value' :label='item.label' :value='item.value')
    span 정렬 : 
    el-select.sorter(v-model='sortValue' placeholder='Select' @change="onChangeSearch()")
      el-option(v-for='item in sort.options' :key='item.value' :label='item.label' :value='item.value')
</template>
<style lang="scss">
.filter.el-select .el-select__tags > span {
  width: 60px;
}
.filter.el-select .el-input__inner {
  width: 80px;
}
.sorter.el-select .el-select__tags > span {
  width: 90px;
}
.sorter.el-select .el-input__inner {
  width: 110px;
}
.filter.el-select,
.sorter.el-select {
  .el-select__tags > span {
    white-space: nowrap;
    overflow: hidden;
    text-overflow: ellipsis;
    display: block;
    text-align: left;
    color: white;
    span {
      display: inline;
    }
  }
}
</style>
<script>
export default {
  props: {
    search: String,
    placeholder: String,
    filter: Object,
    sort: Object,
  },
  data() {
    return {
      searchInput: this.search,
      filterValue: this.filter.value,
      sortValue: this.sort.value,
    }
  },
  computed: {
    filterSelected() {
      return this.filterValue[0] !== 'All' ? 'selected' : 'empty'
    },
  },
  methods: {
    onChangeSearch() {
      this.$emit('change', {
        searchInput: this.searchInput,
        filterValue: this.filterValue,
        sortValue: this.sortValue,
      })
    },
    onFilterChange() {
      const last = this.filterValue[this.filterValue.length - 1]
      if (last === 'All' || !this.filterValue.length) {
        this.filterValue = ['All']
      } else if (last !== 'All' && this.filterValue[0] === 'All') {
        this.filterValue.shift()
      }
      this.onChangeSearch()
    },
  },
}
</script>
