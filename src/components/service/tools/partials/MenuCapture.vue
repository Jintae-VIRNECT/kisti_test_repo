<template>
  <menu-button
    text="캡쳐 후 공유"
    :active="status"
    :disabled="disabled"
    :src="require('assets/image/ic_capture.svg')"
    @click.native="capture"
  ></menu-button>
</template>

<script>
import toolMixin from './toolMixin'
export default {
  name: 'CaptureMenu',
  mixins: [toolMixin],
  data() {
    return {
      status: false,
    }
  },
  watch: {},
  methods: {
    capture() {
      if (this.disabled) return
      this.status = true
      this.$eventBus.$emit('capture')
      setTimeout(() => {
        this.status = false
      }, 100)
    },
  },

  /* Lifecycles */
  beforeDestroy() {},
  mounted() {},
}
</script>
