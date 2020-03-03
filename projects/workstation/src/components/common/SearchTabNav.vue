<template>
  <div>
    <el-input
      class="tool search"
      ref="search"
      :placeholder="placeholder"
      v-model="searchInput"
      @change="onChangeSearch()"
    >
      <el-button slot="append" icon="el-icon-search"></el-button>
    </el-input>
    <span>필터 :</span>
    <el-select
      class="filter"
      v-model="filterValue"
      multiple
      placeholder="Select"
      @change="onFilterChange(filterValue)"
      v-bind:class="filterSelected"
      collapse-tags
    >
      <el-option
        v-for="item in filter.options"
        :key="item.value"
        :label="item.label"
        :value="item.value"
      ></el-option>
    </el-select>
    <span>정렬 :</span>
    <el-select
      class="sorter"
      v-model="sortValue"
      placeholder="Select"
      @change="onChangeSearch()"
    >
      <el-option
        v-for="item in sort.options"
        :key="item.value"
        :label="item.label"
        :value="item.value"
      ></el-option>
    </el-select>
  </div>
</template>

<script>
/**
 * 검색어 input, 필터 dropdown, 정렬 dropdown 으로 이루어진 컴포넌트
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
      /**
       * 변경이 일어나면 부모에게 검색 파라미터를 보낸다
       * @property {object} { search, filter, sort }
       */
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
    search.style.width = this.placeholder.length * 7 + 65 + 'px'
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
