/**
 * 검색 API 공통 mixin
 * 페이지에서 Searchbar 컴포넌트들에 ref를 달아주고 이 mixin을 임포트
 * 컴포넌트들의 값이 변경되면 changedSearchParams() 이벤트가 발생하고 searchParams{} 의 값이 변경된다.
 */

export default {
  data() {
    return {
      searchParams: {},
    }
  },
  methods: {
    emitChangedSearchParams(customParams = {}) {
      const { filter, sort, keyword, page } = this.$refs
      if (this.changedSearchParams) {
        this.$nextTick(() => {
          this.searchParams = {
            ...this.searchParams,
            search: keyword && keyword.value,
            filter: filter && filter.value.join(','),
            sort: sort && sort.value,
            page: page && page.value,
            ...customParams,
          }
          // null 삭제
          Object.keys(this.searchParams).forEach(key => {
            if (!this.searchParams[key]) delete this.searchParams[key]
          })
          this.changedSearchParams(this.searchParams)
        })
      }
    },
  },
  async mounted() {
    await new Promise(_ => setTimeout(_, 100)) // ssr bug
    const { filter, sort, keyword, page, table, mine } = this.$refs

    if (keyword)
      keyword.$on('change', val =>
        this.emitChangedSearchParams({ search: val }),
      )
    if (sort)
      sort.$on('change', val => this.emitChangedSearchParams({ sort: val }))
    if (page)
      page.$on('change', val => this.emitChangedSearchParams({ page: val }))
    if (filter) {
      filter.$on('change', () => {
        const last = filter.myValue[filter.myValue.length - 1]
        if (last === 'ALL' || !filter.myValue.length) {
          filter.myValue = ['ALL']
        } else if (last !== 'ALL' && filter.myValue[0] === 'ALL') {
          filter.myValue.shift()
        }
        this.emitChangedSearchParams({ page: 1 })
      })
    }
    if (table) {
      table.$on('sort-change', ({ prop, order }) => {
        if (!order) this.emitChangedSearchParams()
        else {
          const sort = `${prop},${order.replace('ending', '')}`
          this.emitChangedSearchParams({ sort })
        }
      })
    }
    if (mine) {
      mine.$on('change', label => {
        if (label === this.$t('common.all')) this.searchParams.mine = false
        else this.searchParams.mine = true
        this.emitChangedSearchParams()
      })
    }
  },
}
