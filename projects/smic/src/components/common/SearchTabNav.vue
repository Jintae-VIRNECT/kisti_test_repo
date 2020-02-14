<template lang="pug">
  div
    el-input.tool.search(:placeholder='placeholder' v-model='searchInput' @change="onChangeSearch()")
      el-button(slot='append' icon='el-icon-search')
    span 필터 : 
    el-select.filter(v-model='filterValue' multiple placeholder='Select' @change="onFilterChange(filterValue)" v-bind:class="filterSelected" collapse-tags)
      el-option(v-for='item in filter.options' :key='item.value' :label='item.label' :value='item.value')
    span 정렬 : 
    el-select.sorter(v-model='sortValue' placeholder='Select' @change="onChangeSearch()")
      el-option(v-for='item in sort.options' :key='item.value' :label='item.label' :value='item.value')
</template>
<style lang="scss">
.filter.el-select .el-input__inner {
  width: 90px;
}
.sorter.el-select .el-input__inner {
  width: 110px;
}
.filter.el-select .el-select__tags > span {
  display: block;
  overflow: hidden;
  color: #fff;
  white-space: nowrap;
  text-overflow: ellipsis;
  span {
    display: inline;
  }
}
.page-nav .search-wrapper .el-select .el-tag {
  margin: 0;
  padding: 0;
  color: #3f465a;
  font-size: 13px;
  background: none;

  &:first-child {
    margin-left: 8px;
  }
  &:last-child:not(:only-child) {
    margin-left: 2px;
    word-spacing: -0.2em;
  }
  .el-icon-close {
    display: none;
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
