const defaultCardHeight = '6.143em'
const mobileCardHeight = '7.2rem'
const defaultThumbStyle = { width: '3em', height: '3em' }
const mobileThumbStyle = { width: '4.3rem', height: '4.3rem' }
const defaultPlacement = 'bottom-start'

export default {
  data() {
    return {
      mobilePlacement: null,
      mobileCardHeight: mobileCardHeight,
      defaultCardHeight: defaultCardHeight,
      mobileThumbStyle: mobileThumbStyle,
      defaultThumbStyle: defaultThumbStyle,

      height: this.defaultCardHeight,
      thumbStyle: this.defaultThumbStyle,
      placement: defaultPlacement,
    }
  },
  methods: {
    setDefaultHeightAndThumbStyle(cardHeight, thumbStyle) {
      this.defaultCardHeight = cardHeight || this.defaultCardHeight
      this.defaultThumbStyle = thumbStyle || this.defaultThumbStyle
    },
    setMobileHeightAndThumbStyle(cardHeight, thumbStyle) {
      this.mobileCardHeight = cardHeight || this.defaultCardHeight
      this.mobileThumbStyle = thumbStyle || this.defaultCardHeight
    },
    setMobilePlacement(placement) {
      this.mobilePlacement = placement
    },
    responsiveMobile() {
      this.height = this.mobileCardHeight
      this.thumbStyle = this.mobileThumbStyle

      if (this.mobilePlacement) this.placement = this.mobilePlacement
    },
    responsiveDefault() {
      this.height = this.defaultCardHeight
      this.thumbStyle = this.defaultThumbStyle

      if (this.mobilePlacement) this.placement = defaultPlacement
    },
    responsive(isMobile) {
      if (isMobile) this.responsiveMobile()
      else this.responsiveDefault()
    },
  },
  watch: {
    isMobile(newVal) {
      this.responsive(newVal)
    },
  },
  mounted() {
    this.responsive(this.isMobile)
  },
}
