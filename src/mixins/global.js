import { mapGetters } from 'vuex'
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
    ...mapGetters(['account', 'workspace', 'deviceType', 'hasLicense']),
    isSafari() {
      const userAgent = navigator.userAgent || ''
      return (
        !userAgent.includes('Chrome') &&
        !userAgent.includes('CriOS') &&
        userAgent.includes('Safari')
      )
    },
    isTablet() {
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

    //모바일 반응형 사이즈 이벤트 함수 : js로 반응형 처리를 해야하는 경우에 사용
    callAndGetMobileResponsiveFunction(fn, resetFn) {
      //call
      if (matchMedia('screen and (max-width: 767px)').matches) fn()
      else resetFn()

      //return event function
      return () => {
        if (matchMedia('screen and (max-width: 767px)').matches) fn()
        else resetFn()
      }
    },
    addEventListenerScreenResize(fn) {
      window.addEventListener('resize', fn)
    },
    removeEventListenerScreenResize(fn) {
      window.addEventListener('resize', fn)
    },
  },
}
