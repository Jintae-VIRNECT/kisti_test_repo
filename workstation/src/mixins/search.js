import NavbarFilter from '@/components/common/navbar/NavbarFilter'
import NavbarSort from '@/components/common/navbar/NavbarSort'

export default {
  components: {
    NavbarFilter,
    NavbarSort,
  },
  mounted() {
    const { filter, sort } = this.$refs

    const emitChangedSearchParams = () => {
      if (this.changedSearchParams) {
        this.changedSearchParams({
          filter: filter.value,
          sort: sort.value,
        })
      }
    }

    filter.$on('change', () => {
      emitChangedSearchParams()
    })
    sort.$on('change', () => {
      emitChangedSearchParams()
    })
  },
}
