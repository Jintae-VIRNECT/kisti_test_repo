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
      this.$emit('update:visible', val)
      this.$nextTick(() => {
        if (val && this.opened) this.opened()
        if (!val && this.closed) this.closed()
      })
    },
  },
}
