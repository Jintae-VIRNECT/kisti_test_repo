export default {
  methods: {
    playTimeRender(playTime) {
      if (playTime === '') {
        playTime = 0
      }
      let sec_num = Number.parseInt(playTime, 10)
      let hours = Math.floor(sec_num / 3600)
      let minutes = Math.floor((sec_num - hours * 3600) / 60)
      let seconds = sec_num - hours * 3600 - minutes * 60

      let hText = this.$t('date.hour')
      let mText = this.$t('date.minute')
      let sText = this.$t('date.second')

      if (hours === 0 && minutes === 0 && seconds < 1) {
        hours = ''
        hText = ''

        minutes = ''
        mText = ''

        seconds = '0'
      } else {
        if (hours === 0) {
          hours = ''
          hText = ''
        }
        if (minutes === 0) {
          minutes = ''
          mText = ''
        }
        if (seconds === 0) {
          seconds = ''
          sText = ''
        }
      }

      return `${hours}${hText} ${minutes}${mText} ${seconds}${sText}`
    },
    fileSizeRender(size) {
      const mb = 1048576 //1 MB

      if (size >= mb) {
        size = size / 1024 / 1024
        return `${size.toFixed(1)}MB`
      } else {
        size = size / 1024
        return `${size.toFixed(1)}KB`
      }
    },
    expirationDateRender(date) {
      return this.$dayjs(date).format('YYYY.MM.DD')
    },
  },
}
