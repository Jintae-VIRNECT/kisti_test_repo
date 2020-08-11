import { mapGetters } from 'vuex'
import dayjs from 'dayjs'

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
        return dayjs(time)
          .utc()
          .format('HH:mm:ss')
      } else {
        return dayjs(time)
          .utc()
          .format('mm:ss')
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
    callStatus: function(value) {
      if (value == 1) {
        return 'online'
      } else if (value == 2) {
        return 'busy'
      } else {
        return 'offline'
      }
    },
    levelChange: function(value) {
      if (value == 'manager') {
        return 2
      } else if (value == 'member') {
        return 1
      }
    },
    capitalize: function(value) {
      if (!value) return ''
      value = value.toString()
      return value.charAt(0).toUpperCase() + value.slice(1)
    },
    initialize: function(value) {
      for (let i = 0; i < value.length; i++) {
        if (/[가-힣一-龥a-z一-龠ぁ-ゔァ-ヴー々〆〤]/i.test(value[i])) {
          return value[i].toUpperCase()
        }
      }

      return value[0]
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
    isScreenApp() {
      // return true
      return navigator.userAgent.toLowerCase().includes('mobileapp')
    },
    timeZoneOffset() {
      return new Date().getTimezoneOffset() / 60
    },
  },
  methods: {
    // TODO: beta check
    checkBeta() {
      this.$toasted.clear()
      this.$toasted.show('베타 버전에서는 지원하지 않는 기능입니다.', {
        position: 'top-center',
        type: 'info',
        duration: 2000,
        action: null,
        containerClass: 'toast-default',
      })

      //may add beta check condition
      return false
    },
    setTimeZone(time) {
      // return this.$moment(time).add(this.timeZoneOffset * -1, 'hours')
    },
    onImageError(event) {
      // console.log(event.target)
      // event.target.src = require('assets/image/img_user_profile.svg')
      event.target.style.display = 'none'
      event.target.classList.add('default')
    },
    onImageErrorGroup(event) {
      // event.target.src = require('assets/image/img_default_group.svg')
      event.target.style.display = 'none'
    },
  },
}
