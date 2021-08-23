export default {
  data() {
    return {
      _responsiveFn: null,
      visiblePcFlag: false,
      visibleMobileFlag: false,
      _visibleFlag: false,
    }
  },
  methods: {
    setVisiblePcFlag() {
      this.visiblePcFlag = this._visibleFlag
      this.visibleMobileFlag = false
    },
    setVisibleMobileFlag() {
      this.visiblePcFlag = false
      this.visibleMobileFlag = this._visibleFlag
    },
    setVisiblePcOrMobileFlag(flag) {
      this._visibleFlag = flag
      this.callAndGetMobileResponsiveFunction(
        this.setVisibleMobileFlag,
        this.setVisiblePcFlag,
      )
    },
  },
  mounted() {
    this._responsiveFn = this.callAndGetMobileResponsiveFunction(
      this.setVisibleMobileFlag,
      this.setVisiblePcFlag,
    )
    this.addEventListenerScreenResize(this._responsiveFn)
  },
  beforeDestroy() {
    this.removeEventListenerScreenResize(this._responsiveFn)
  },
}
