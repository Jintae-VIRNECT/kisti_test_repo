import { mapGetters } from 'vuex'
import dayjs from 'dayjs'
import { RUNTIME_ENV, RUNTIME, SETTINGS } from 'configs/env.config'

export default {
  data() {
    return {}
  },
  filters: {
    timeFilter(time) {
      if (time === 0) {
        return '00:00'
      }
      if (!time) {
        return null
      }
      if (time >= 60 * 60 * 1000) {
        return dayjs(time).utc().format('HH:mm:ss')
      } else {
        return dayjs(time).utc().format('mm:ss')
      }
    },
    networkStatus(status) {
      if (status === 'good') {
        return '우수'
      } else if (status === 'normal') {
        return '양호'
      } else if (status === 'bad') {
        return '약함'
      }
    },
    convertTime(duration) {
      let sec_num = Number.parseInt(duration, 10)
      let hours = Math.floor(sec_num / 3600)
      let minutes = Math.floor((sec_num - hours * 3600) / 60)
      let seconds = sec_num - hours * 3600 - minutes * 60

      let hText = '시 '
      let mText = '분 '
      let sText = '초'

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

      return hours + hText + minutes + mText + seconds + sText
    },
    convertSize(size) {
      const mb = 1048576

      if (size >= mb) {
        size = size / 1024 / 1024
        return `${size.toFixed(1)}MB`
      } else {
        size = size / 1024
        return `${size.toFixed(1)}KB`
      }
    },
  },
  computed: {
    ...mapGetters(['account', 'workspace', 'deviceType', 'hasLicense']),
    isMobileChrome() {
      const userAgent = navigator.userAgent
      const isChromeMobile =
        userAgent.includes('Mobile') ||
        userAgent.includes('CriOS') ||
        userAgent.includes('mobileApp')
      return isChromeMobile
    },
    isScreenDesktop() {
      return 'desktop' === this.deviceType
    },
    isScreenTablet() {
      return 'tablet' === this.deviceType
    },
    isScreenMobile() {
      return 'mobile' === this.deviceType
    },
    isOnpremise() {
      return RUNTIME_ENV === RUNTIME.ONPREMISE ? true : false
    },
    allowServerRecordFileInfo() {
      return SETTINGS.ALLOW_SERVER_RECORD_FILE_INFO
    },
    allowLocalRecordFileInfo() {
      return SETTINGS.ALLOW_LOCAL_RECORD_FILE_INFO
    },
    allowAttachFileInfo() {
      return SETTINGS.ALLOW_ATTACH_FILE_INFO
    },
  },
  methods: {
    unsupport() {
      this.$toasted.show(this.$t('confirm.unsupport_feature'), {
        position: 'bottom-center',
        duration: 5000,
        action: {
          icon: 'close',
          onClick: (e, toastObject) => {
            toastObject.goAway(0)
          },
        },
      })
    },
    onImageError(event) {
      // console.log(event.target)
      // event.target.src = require('assets/image/img_user_profile.svg')
      event.target.style.display = 'none'
      // event.target.classList.add('default')
    },
    onImageErrorGroup(event) {
      // event.target.src = require('assets/image/img_default_group.svg')
      event.target.style.display = 'none'
    },
  },
}
