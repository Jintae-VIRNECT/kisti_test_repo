export default {
  props: {
    visible: Boolean,
  },
  methods: {
    handleClose() {
      this.$emit('update:visible', false)
    },
  },
}
