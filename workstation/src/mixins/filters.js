import { filters as dayjsFilters } from '@/plugins/dayjs'
import { targetTypes } from '@/models/content/Content'
import { app } from '@/plugins/context'

export default {
  filters: {
    ...dayjsFilters,
    byte2mb(bytes) {
      const kb = bytes / 1024
      const mb = kb / 1024
      return mb < 1
        ? Math.round(kb * 100) / 100 + ' KB'
        : Math.round(mb * 100) / 100 + ' MB'
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
    targetType2label(targetType) {
      if (!targetType) return '-'
      const targetInfo = targetTypes.find(({ value }) => value === targetType)
      return app.i18n.t(targetInfo.label)
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
