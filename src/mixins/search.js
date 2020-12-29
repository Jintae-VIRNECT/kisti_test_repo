import SearchbarSort from '@/components/common/searchbar/SearchbarSort'
import SearchbarKeyword from '@/components/common/searchbar/SearchbarKeyword'
import SearchbarPage from '@/components/common/searchbar/SearchbarPage'

export default {
  components: {
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
    emitChangedSearchParams(customParams = {}) {
      const { sort, keyword, page } = this.$refs
      if (this.changedSearchParams) {
        this.$nextTick(() => {
          this.searchParams = {
            ...this.searchParams,
            search: keyword && keyword.value,
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
    },
  },
  mounted() {
    const { sort, keyword, page, table } = this.$refs

    if (keyword) keyword.$on('change', this.emitChangedSearchParams)
    if (sort) sort.$on('change', this.emitChangedSearchParams)
    if (page) page.$on('change', this.emitChangedSearchParams)
    if (table) {
      table.$on('sort-change', ({ prop, order }) => {
        if (!order) this.emitChangedSearchParams()
        else {
          const sort = `${prop},${order.replace('ending', '')}`
          this.emitChangedSearchParams({ sort })
        }
      })
    }
  },
}
