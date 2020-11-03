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
      return (mb / 1024).toFixed(2) * 1
    },
    /**
     * cdn files filter, onpremise minio proxy url
     * @param {string} url
     */
    cdn(url) {
      if (url && 'minio' in this.$url) {
        url = url.replace(/^((http[s]?|ftp):\/\/)([^/]+)/, this.$url['minio'])
      }
      return url
    },
  },
}
