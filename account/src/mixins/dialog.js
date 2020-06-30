export default {
  props: {
    visible: Boolean,
  },
  watch: {
    visible(val) {
      this.$emit('update:visible', val)
      this.$nextTick(() => {
        if (val && this.opened) this.opened()
        if (!val && this.closed) this.closed()
      })
    },
  },
  methods: {
    handleClose() {
      this.$emit('update:visible', false)
    },
  },
}
