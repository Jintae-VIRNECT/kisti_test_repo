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
