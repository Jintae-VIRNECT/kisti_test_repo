export default {
  data() {
    return {
      responsiveFn: null,
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
    this.responsiveFn = this.callAndGetMobileResponsiveFunction(
      this.setVisibleMobileFlag,
      this.setVisiblePcFlag,
    )
    this.addEventListenerScreenResize(this.responsiveFn)
  },
  beforeDestroy() {
    this.removeEventListenerScreenResize(this.responsiveFn)
  },
}
