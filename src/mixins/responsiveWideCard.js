const defaultWideCardHeight = '6.143em'
const mobileWideCardHeight = '7.2rem'
const defaultThumbStyle = { width: '3em', height: '3em' }
const mobileThumbStyle = { width: '4.3rem', height: '4.3rem' }

export default {
  data() {
    return {
      height: defaultWideCardHeight,
      thumbStyle: defaultThumbStyle,
      responsiveFn: null,
    }
  },
  methods: {
    responsiveMobile() {
      this.height = mobileWideCardHeight
      this.thumbStyle = mobileThumbStyle
    },
    responsiveDefault() {
      this.height = defaultWideCardHeight
      this.thumbStyle = defaultThumbStyle
    },
  },
  mounted() {
    this.responsiveFn = this.callAndGetMobileResponsiveFunction(
      this.responsiveMobile,
      this.responsiveDefault,
    )
    this.addEventListenerScreenResize(this.responsiveFn)
  },
  beforeDestroy() {
    this.removeEventListenerScreenResize(this.responsiveFn)
  },
}
