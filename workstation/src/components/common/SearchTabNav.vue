<template>
  <div class="search-tab-nav">
    <el-input
      ref="search"
      class="search"
      v-if="placeholder"
      :placeholder="$t(placeholder)"
      v-model="searchValue"
      @change="submit()"
    >
    </el-input>
    <el-select
      ref="filter"
      class="filter"
      v-if="filter"
      v-bind:class="filterSelected"
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
    <el-select
      ref="sort"
      class="sort"
      v-if="sort"
      v-model="sortValue"
      @change="submit()"
    >
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
 * 검색어 input, 필터 dropdown, 정렬 dropdown 으로 이루어진 컴포넌트.
 * 텍스트는 i18n key 값을 넣어주면 됩니다.
 *
 * __require__ element-ui
 */
export default {
  props: {
    /**
     * 검색어 설명. 없으면 input DOM 렌더링 X
     */
    placeholder: String,
    /**
     * 검색어 초기값
     */
    search: String,
    /**
     * 여러개 동시 선택 가능한 필터. 없으면 DOM 렌더링 X
     * @values { value: ['ALL'], options: [{ value, label }] }
     */
    filter: Object,
    /**
     * 여러개 동시 선택 가능한 필터. 없으면 DOM 렌더링 X
     * @values { value: 'createdAt,desc', options: [{ value, label }] }
     */
    sort: Object,
    /**
     * 컨텐츠 크기에 맞춰서 width 계산 여부
     */
    autoResize: {
      type: Boolean,
      default: true,
    },
  },
  data() {
    return {
      searchValue: this.search,
      filterValue: this.filter ? this.filter.value : [],
      sortValue: this.sort ? this.sort.value : '',
    }
  },
  computed: {
    filterSelected() {
      return this.filterValue[0].toLowerCase() !== 'all' ? 'selected' : 'empty'
    },
  },
  watch: {
    search(val) {
      this.searchValue = val
    },
    filter(val) {
      this.filterValue = val.value
      filterChange()
    },
    sort(val) {
      this.sortValue = val.value
      submit()
    },
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
      const params = {
        search: this.searchValue,
        filter: this.filterValue && this.filterValue.join(','),
        sort: this.sortValue,
      }
      if (this.filter) {
        const withoutAllOptions = this.filter.options.filter(
          option => option.value !== 'ALL',
        )
        if (this.filterValue.length === withoutAllOptions.length) {
          params.filter = 'ALL'
        }
      }
      /**
       * 변경이 일어나면 부모에게 검색 파라미터를 보낸다
       * @property {object} { search: '멍멍이', filter: 'CAT,DOG', sort: 'createdAt,desc' }
       */
      this.$emit('submit', params)
    },
    /**
     * 인풋 컨텐츠 크기에 맞춰 리사이징
     */
    resize() {
      const { search, filter, sort } = this.$refs
      if (this.placeholder) {
        search.$el.style['width'] =
          this.$t(this.placeholder).length * 14 + 10 + 'px'
      }
      // filter auto width
      if (this.filter) {
        const filterWidth = this.filter.options.reduce((max, option) => {
          const translated = this.$t(option.label)
          return translated.length > max ? translated.length : max
        }, 0)
        filter.$el.style['width'] = filterWidth * 14 + 70 + 'px'
      }
      // filter auto width
      if (this.sort) {
        const sortWidth = this.sort.options.reduce((max, option) => {
          const translated = this.$t(option.label)
          return translated.length > max ? translated.length : max
        }, 0)
        sort.$el.style['width'] = sortWidth * 14 + 40 + 'px'
      }
    },
  },
  mounted() {
    if (this.autoResize) this.resize()
  },
}
</script>

<style lang="scss">
.search-tab-nav {
  .search,
  .filter,
  .sort {
    width: 150px;
  }
  .el-select .el-tag {
    font-size: 1em;
    background: none;
    border: none;
    .el-icon-close {
      display: none;
    }
  }
  .filter.selected {
    .el-input__inner {
      background: #455163;
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
