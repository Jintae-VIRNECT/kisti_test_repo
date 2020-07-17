import { filters as dayjsFilters } from '@/plugins/dayjs'

export default {
  filters: {
    ...dayjsFilters,
    byte2mb(bytes) {
      const kb = bytes / 1024
      const mb = kb / 1024
      return mb < 1
        ? Math.round(kb * 100) / 100 + 'kB'
        : Math.round(mb * 100) / 100 + 'MB'
    },
    mb2gb(mb) {
      return (mb / 1024).toFixed(2) * 1
    },
  },
  methods: {
    boolCheck(bool) {
      if (bool > 0) return true
      else return bool && bool.toLowerCase() !== 'no'
    },
    toNumbersString(numbers) {
      return (
        numbers &&
        `${numbers.toLocaleString()} ${this.$t('common.numbersUnit')}`
      )
    },
  },
}
