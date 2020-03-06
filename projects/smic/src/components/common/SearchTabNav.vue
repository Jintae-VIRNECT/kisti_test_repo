<template lang="pug">
  div
    el-input.tool.search(
      ref="search" 
      :placeholder="placeholder" 
      v-model="searchInput" 
      @change="onChangeSearch()"
    )
      el-button(slot="append" icon="el-icon-search")
    span 필터 : 
    el-select.filter(
      v-model="filterValue" 
      multiple placeholder="Select" 
      @change="onFilterChange(filterValue)" 
      v-bind:class="filterSelected" 
      collapse-tags
    )
      el-option(v-for="item in filter.options" :key="item.value" :label="item.label" :value="item.value")
    span 정렬 : 
    el-select.sorter(
      v-model="sortValue" 
      placeholder="Select" 
      @change="onChangeSearch()"
    )
      el-option(v-for="item in sort.options" :key="item.value" :label="item.label" :value="item.value")
</template>

<script>
/**
 * 검색어 input, 필터 dropdown, 정렬 dropdown 으로 이루어진 컴포넌트
 *
 * __require__ element-ui
 * <br>__used__ smic
 */
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
        search: this.searchInput,
        filter: this.filterValue.map(value => value.toUpperCase()).join(),
        sort: this.sortValue,
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
  mounted() {
    const search = this.$refs.search.$el
    search.style.width = this.placeholder.length * 8 + 60 + 'px'
  },
}
</script>

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
