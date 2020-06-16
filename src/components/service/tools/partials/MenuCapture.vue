<template>
  <menu-button
    text="캡쳐 후 공유"
    :active="status"
    :src="require('assets/image/ic_capture.svg')"
    @click="capture"
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
  computed: {},
  watch: {},
  methods: {
    capture() {
      this.status = !this.status
    },
  },

  /* Lifecycles */
  beforeDestroy() {},
  mounted() {},
}
</script>
