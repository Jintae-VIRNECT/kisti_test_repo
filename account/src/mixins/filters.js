import { filters as dayjsFilters } from '@/plugins/dayjs'

export default {
  filters: {
    ...dayjsFilters,
  },
  methods: {
    toPriceString(price) {
      return (
        price && `${price.toLocaleString()} ${this.$t('payment.monetaryUnit')}`
      )
    },
    boolCheck(bool) {
      if (bool > 0) return true
      else return bool && bool.toLowerCase() !== 'no'
    },
    mb2gb(mb) {
      return (mb / 1000).toFixed(2) * 1
    },
  },
}
