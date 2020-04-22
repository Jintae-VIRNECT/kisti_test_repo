import { mapGetters } from 'vuex'

export default {
  components: {},
  filters: {
    call_status: function(value) {
      if (value == 1) {
        return 'online'
      } else if (value == 2) {
        return 'busy'
      } else {
        return 'offline'
      }
    },
    level: function(value) {
      if (value == 1) {
        return 'master'
      } else if (value == 2) {
        return 'manager'
      } else if (value == 3) {
        return 'member'
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
    ...mapGetters(['account', 'deviceType']),
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
    setTimeZone(time) {
      return this.$moment(time).add(this.timeZoneOffset * -1, 'hours')
    },
    onImageError(event) {
      event.target.src = require('assets/image/img_user_profile.svg')
    },
  },
}
