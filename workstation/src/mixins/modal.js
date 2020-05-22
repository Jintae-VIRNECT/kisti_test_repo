export default {
  props: {
    visible: Boolean,
  },
  data() {
    return {
      showMe: false,
    }
  },
  watch: {
    visible(val) {
      this.showMe = val
    },
    showMe(val) {
      if (val && this.opend) this.opend()
      if (!val && this.closed) this.closed()
      this.$emit('update:visible', val)
    },
  },
}
