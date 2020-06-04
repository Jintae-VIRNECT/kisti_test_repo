import { filters as dayjsFilters } from '@/plugins/dayjs'

export default {
  filters: {
    ...dayjsFilters,
    toMegaBytes(bytes) {
      const kb = bytes / 1024
      const mb = kb / 1024
      return mb < 1
        ? Math.round(kb * 100) / 100 + 'kB'
        : Math.round(mb * 100) / 100 + 'MB'
    },
  },
  methods: {
    boolCheck(bool) {
      if (bool > 0) return true
      else return bool && bool.toLowerCase() !== 'no'
    },
  },
}
