const defaultCardHeight = '6.143em'
const mobileCardHeight = '7.2rem'
const defaultThumbStyle = { width: '3em', height: '3em' }
const mobileThumbStyle = { width: '4.3rem', height: '4.3rem' }
const defaultPlacement = 'bottom-start'

export default {
  data() {
    return {
      mobilePlacement: null,
      mobileCardHeight: null,
      defaultCardHeight: null,
      mobileThumbStyle: null,
      defaultThumbStyle: null,
      height: defaultCardHeight,
      thumbStyle: defaultThumbStyle,
      _responsiveFn: null,
      placement: defaultPlacement,
    }
  },
  methods: {
    setDefaultHeightAndThumbStyle(cardHeight, thumbStyle) {
      this.defaultCardHeight = cardHeight || defaultCardHeight
      this.defaultThumbStyle = thumbStyle || defaultThumbStyle
    },
    setMobileHeightAndThumbStyle(cardHeight, thumbStyle) {
      this.mobileCardHeight = cardHeight || defaultCardHeight
      this.mobileThumbStyle = thumbStyle || defaultCardHeight
    },
    setMobilePlacement(placement) {
      this.mobilePlacement = placement
    },
    responsiveMobile() {
      this.height = this.mobileCardHeight || mobileCardHeight
      this.thumbStyle = this.mobileThumbStyle || mobileThumbStyle

      if (this.mobilePlacement) this.placement = this.mobilePlacement
    },
    responsiveDefault() {
      this.height = this.defaultCardHeight || defaultCardHeight
      this.thumbStyle = this.defaultThumbStyle || defaultThumbStyle

      if (this.mobilePlacement) this.placement = defaultPlacement
    },
  },
  mounted() {
    this._responsiveFn = this.callAndGetMobileResponsiveFunction(
      this.responsiveMobile,
      this.responsiveDefault,
    )
    this.addEventListenerScreenResize(this._responsiveFn)
  },
  beforeDestroy() {
    this.removeEventListenerScreenResize(this._responsiveFn)
  },
}
