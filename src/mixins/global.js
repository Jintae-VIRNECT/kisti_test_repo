import { mapGetters, mapMutations } from 'vuex'
import dayjs from 'dayjs'
import { RUNTIME, RUNTIME_ENV } from 'configs/env.config'

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
    ...mapGetters([
      'account',
      'workspace',
      'deviceType',
      'hasLicense',
      'isMobileSize',
      'isTabletSize',
    ]),
    isSafari() {
      const userAgent = navigator.userAgent || ''
      return (
        !userAgent.includes('Chrome') &&
        !userAgent.includes('CriOS') &&
        userAgent.includes('Safari')
      )
    },
    isMobileDevice() {
      const userAgent = navigator.userAgent || ''
      const isIpadSafari =
        !userAgent.includes('Chrome') &&
        !userAgent.includes('CriOS') &&
        userAgent.includes('Safari') &&
        navigator.maxTouchPoints !== 0
      return (
        userAgent.includes('Mobile') ||
        userAgent.includes('Android') ||
        userAgent.includes('mobileApp') ||
        isIpadSafari
      )
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
    //구축형 여부
    isOnpremise() {
      return RUNTIME.ONPREMISE === RUNTIME_ENV
    },
  },
  methods: {
    ...mapMutations(['SET_IS_MOBILE_SIZE', 'SET_IS_TABLET_SIZE']),
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
    responsiveGlobal() {
      if (matchMedia('screen and (max-width: 767px)').matches) {
        this.SET_IS_MOBILE_SIZE(true)
        this.SET_IS_TABLET_SIZE(false)
      } else if (
        matchMedia(
          'only screen and (min-device-width: 768px) and (max-width: 1023px)',
        ).matches
      ) {
        this.SET_IS_MOBILE_SIZE(false)
        this.SET_IS_TABLET_SIZE(true)
      } else {
        this.SET_IS_MOBILE_SIZE(false)
        this.SET_IS_TABLET_SIZE(false)
      }
    },
  },
  mounted() {
    this.responsiveGlobal()
    window.addEventListener('resize', this.responsiveGlobal)
  },
  beforeDestroy() {
    window.removeEventListener('resize', this.responsiveGlobal)
  },
}
