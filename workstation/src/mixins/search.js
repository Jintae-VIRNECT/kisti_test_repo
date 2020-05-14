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
  methods: {
    emitChangedSearchParams(customParams) {
      const { filter, sort, keyword, page } = this.$refs
      if (this.changedSearchParams) {
        this.$nextTick(() => {
          this.searchParams = {
            ...this.searchParams,
            search: keyword && keyword.value,
            filter: filter && filter.value.join(','),
            sort: (customParams && customParams.sort) || (sort && sort.value),
            page: page && page.value,
          }
          this.changedSearchParams(this.searchParams)
        })
      }
    },
  },
  mounted() {
    const { filter, sort, keyword, page, table } = this.$refs

    if (keyword) keyword.$on('change', this.emitChangedSearchParams)
    if (sort) sort.$on('change', this.emitChangedSearchParams)
    if (page) page.$on('change', this.emitChangedSearchParams)
    if (filter)
      filter.$on('change', () => {
        const last = filter.myValue[filter.myValue.length - 1]
        if (last === 'ALL' || !filter.myValue.length) {
          filter.myValue = ['ALL']
        } else if (last !== 'ALL' && filter.myValue[0] === 'ALL') {
          filter.myValue.shift()
        }
        this.emitChangedSearchParams()
      })
    if (table)
      table.$on('sort-change', ({ prop, order }) => {
        if (!order) this.emitChangedSearchParams()
        else {
          const sort = `${prop},${order.replace('ending', '')}`
          this.emitChangedSearchParams({ sort })
        }
      })
  },
}
