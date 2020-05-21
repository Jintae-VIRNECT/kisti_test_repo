import SearchbarFilter from '@/components/common/searchbar/SearchbarFilter'
import SearchbarSort from '@/components/common/searchbar/SearchbarSort'
import SearchbarKeyword from '@/components/common/searchbar/SearchbarKeyword'
import SearchbarPage from '@/components/common/searchbar/SearchbarPage'

/**
 *
 */
export default {
  components: {
    SearchbarFilter,
    SearchbarSort,
    SearchbarKeyword,
    SearchbarPage,
  },
  data() {
    return {
      searchParams: {},
    }
  },
  mounted() {
    const { filter, sort, keyword, page, table } = this.$refs

    const emitChangedSearchParams = customParams => {
      if (this.changedSearchParams) {
        this.$nextTick(() => {
          this.searchParams = {
            search: keyword && keyword.value,
            filter: filter && filter.value.join(','),
            sort: (customParams && customParams.sort) || (sort && sort.value),
            page: page && page.value,
          }
          // null 삭제
          Object.keys(this.searchParams).forEach(key => {
            if (!this.searchParams[key]) delete this.searchParams[key]
          })
          this.changedSearchParams(this.searchParams)
        })
      }
    }

    if (keyword) keyword.$on('change', emitChangedSearchParams)
    if (sort) sort.$on('change', emitChangedSearchParams)
    if (page) page.$on('change', emitChangedSearchParams)
    if (filter)
      filter.$on('change', () => {
        const last = filter.myValue[filter.myValue.length - 1]
        if (last === 'ALL' || !filter.myValue.length) {
          filter.myValue = ['ALL']
        } else if (last !== 'ALL' && filter.myValue[0] === 'ALL') {
          filter.myValue.shift()
        }
        emitChangedSearchParams()
      })
    if (table)
      table.$on('sort-change', ({ prop, order }) => {
        if (!order) emitChangedSearchParams()
        else {
          const sort = `${prop},${order.replace('ending', '')}`
          emitChangedSearchParams({ sort })
        }
      })
  },
}
