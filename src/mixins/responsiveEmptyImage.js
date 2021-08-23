export default {
  data() {
    return {
      _mobileEmptyImage: null,
      _defaultEmptyImage: null,
      _responsiveEmptyImageFn: null,
      emptyImage: null,
    }
  },
  methods: {
    setMobileDefaultEmptyImage(defaultEmptyImage, mobileEmptyImage) {
      this._defaultEmptyImage = defaultEmptyImage
      this._mobileEmptyImage = mobileEmptyImage
    },
    setMobileEmptyImage() {
      this.emptyImage = this._mobileEmptyImage
    },
    setDefaultEmptyImage() {
      this.emptyImage = this._defaultEmptyImage
    },
  },
  mounted() {
    this._responsiveEmptyImageFn = this.callAndGetMobileResponsiveFunction(
      this.setMobileEmptyImage,
      this.setDefaultEmptyImage,
    )
    this.addEventListenerScreenResize(this._responsiveEmptyImageFn)
  },
  beforeDestroy() {
    this.removeEventListenerScreenResize(this._responsiveEmptyImageFn)
  },
}
