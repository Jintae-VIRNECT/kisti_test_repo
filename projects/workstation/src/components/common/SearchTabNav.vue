<template>
  <div class="search-tab-nav">
    <el-input
      class="search"
      :placeholder="placeholder"
      v-model="searchValue"
      @change="submit()"
    >
    </el-input>
    <el-select
      class="filter"
      v-model="filterValue"
      @change="filterChange()"
      multiple
      collapse-tags
    >
      <el-option
        v-for="item in filter.options"
        :key="item.value"
        :label="$t(item.label)"
        :value="item.value"
      />
    </el-select>
    <el-select class="sort" v-model="sortValue" @change="submit()">
      <el-option
        v-for="item in sort.options"
        :key="item.value"
        :label="$t(item.label)"
        :value="item.value"
      />
    </el-select>
  </div>
</template>

<script>
/**
 * 검색어 input, 필터 dropdown, 정렬 dropdown 으로 이루어진 컴포넌트
 */
export default {
  props: {
    placeholder: String,
    search: String,
    filter: Object,
    sort: Object,
  },
  data() {
    return {
      searchValue: this.search,
      filterValue: this.filter.value,
      sortValue: this.sort.value,
    }
  },
  methods: {
    filterChange() {
      const last = this.filterValue[this.filterValue.length - 1]
      if (last === 'ALL' || !this.filterValue.length) {
        this.filterValue = ['ALL']
      } else if (last !== 'ALL' && this.filterValue[0] === 'ALL') {
        this.filterValue.shift()
      }
      this.submit()
    },
    submit() {
      /**
       * 변경이 일어나면 부모에게 검색 파라미터를 보낸다
       * @property {object} { search, filter, sort }
       */
      const params = {
        search: this.searchValue,
        filter: this.filterValue.join(','),
        sort: this.sortValue,
      }
      const withoutAllOptions = this.filter.options.filter(
        option => option.value !== 'ALL',
      )
      if (this.filterValue.length === withoutAllOptions.length) {
        params.filter = 'ALL'
      }
      this.$emit('submit', params)
    },
  },
}
</script>
