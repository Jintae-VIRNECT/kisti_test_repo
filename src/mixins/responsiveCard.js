const defaultWideCardHeight = '6.143em'
const mobileWideCardHeight = '7.2rem'
const defaultThumbStyle = { width: '3em', height: '3em' }
const mobileThumbStyle = { width: '4.3rem', height: '4.3rem' }
const defaultPlacement = 'bottom-start'

export default {
  data() {
    return {
      mobilePlacement: null,
      mobileWideCardHeight: null,
      mobileThumbStyle: null,
      height: defaultWideCardHeight,
      thumbStyle: defaultThumbStyle,
      responsiveFn: null,
      placement: defaultPlacement,
    }
  },
  methods: {
    setMobileHeightAndThumbStyle(wideCardHeight, thumbStyle) {
      this.mobileWideCardHeight = wideCardHeight
      this.mobileThumbStyle = thumbStyle
    },
    setMobilePlacement(placement) {
      this.mobilePlacement = placement
    },
    responsiveMobile() {
      this.height = this.mobileWideCardHeight || mobileWideCardHeight
      this.thumbStyle = this.mobileThumbStyle || mobileThumbStyle

      if (this.mobilePlacement) this.placement = this.mobilePlacement
    },
    responsiveDefault() {
      this.height = defaultWideCardHeight
      this.thumbStyle = defaultThumbStyle

      if (this.mobilePlacement) this.placement = this.defaultPlacement
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
