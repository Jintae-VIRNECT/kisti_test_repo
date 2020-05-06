<template>
  <tool-button
    text="캡쳐 후 공유"
    :active="tool === 'capture'"
    :src="require('assets/image/call/icn_capture.png')"
    @action="capture"
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
      this.setTool('capture')
    },
  },

  /* Lifecycles */
  beforeDestroy() {},
  mounted() {},
}
</script>
