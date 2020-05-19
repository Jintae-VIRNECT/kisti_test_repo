<template>
  <menu-button
    text="캡쳐 후 공유"
    :active="action === 'capture'"
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
