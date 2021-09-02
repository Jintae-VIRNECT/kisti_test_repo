export default {
  data() {
    return {
      _mobileEmptyImage: null,
      _defaultEmptyImage: null,
      emptyImage: this._defaultEmptyImage,
    }
  },
  watch: {
    isMobileSize(newVal) {
      if (newVal) this.setMobileEmptyImage()
      else this.setDefaultEmptyImage()
    },
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
    responsiveEmptyImage(isMobileSize) {
      if (isMobileSize) this.setMobileEmptyImage()
      else this.setDefaultEmptyImage()
    },
  },
  mounted() {
    this.responsiveEmptyImage(this.isMobileSize)
  },
}
