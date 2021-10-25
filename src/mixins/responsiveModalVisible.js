export default {
  data() {
    return {
      visiblePcFlag: false,
      visibleMobileFlag: false,
    }
  },
  watch: {
    isMobileSize: {
      immediate: true,
      handler: function(newVal) {
        if (newVal) this.setVisibleMobileFlag(this.visible)
        else this.setVisiblePcFlag(this.visible)
      },
    },
  },
  methods: {
    setVisiblePcFlag(flag) {
      this.visiblePcFlag = flag
      this.visibleMobileFlag = false
    },
    setVisibleMobileFlag(flag) {
      this.visiblePcFlag = false
      this.visibleMobileFlag = flag
    },
    setVisiblePcOrMobileFlag(flag) {
      if (this.isMobileSize) this.setVisibleMobileFlag(flag)
      else this.setVisiblePcFlag(flag)
    },
  },
}
