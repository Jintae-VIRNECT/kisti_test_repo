<template>
  <tool-button
    text="캡쳐 후 공유"
    :active="action === 'capture'"
    :src="require('assets/image/call/icn_capture.png')"
    @click="capture"
  ></tool-button>
</template>

<script>
import toolMixin from './toolMixin'
export default {
  name: 'CaptureTool',
  mixins: [toolMixin],
  data() {
    return {}
  },
  computed: {},
  watch: {},
  methods: {
    capture() {
      this.setAction('capture')
    },
  },

  /* Lifecycles */
  beforeDestroy() {},
  mounted() {},
}
</script>
