import SearchbarFilter from '@/components/common/searchbar/SearchbarFilter'
import SearchbarSort from '@/components/common/searchbar/SearchbarSort'
import SearchbarKeyword from '@/components/common/searchbar/SearchbarKeyword'
import SearchbarPage from '@/components/common/searchbar/SearchbarPage'

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
    const { filter, sort, keyword, page } = this.$refs

    const emitChangedSearchParams = () => {
      if (this.changedSearchParams) {
        this.$nextTick(() => {
          this.searchParams = {
            search: keyword && keyword.value,
            filter: filter && filter.value.join(','),
            sort: sort && sort.value,
            page: page && page.value,
          }
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
  },
}
