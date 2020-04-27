import NavbarFilter from '@/components/common/navbar/NavbarFilter'
import NavbarSort from '@/components/common/navbar/NavbarSort'

export default {
  components: {
    NavbarFilter,
    NavbarSort,
  },
  data() {
    return {
      searchParams: {},
    }
  },
  mounted() {
    const { filter, sort } = this.$refs

    const emitChangedSearchParams = () => {
      if (this.changedSearchParams) {
        this.$nextTick(() => {
          this.searchParams = {
            filter: filter.value.join(','),
            sort: sort.value,
          }
          this.changedSearchParams(this.searchParams)
        })
      }
    }

    sort.$on('change', () => {
      emitChangedSearchParams()
    })

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
