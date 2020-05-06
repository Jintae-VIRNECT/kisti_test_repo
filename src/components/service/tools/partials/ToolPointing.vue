<template>
  <tool-button
    text="포인팅"
    :active="tool === 'pointing'"
    :src="require('assets/image/call/ic_pointing.png')"
    @action="pointing"
  ></tool-button>
</template>

<script>
import toolMixin from './toolMixin'
export default {
  name: 'PointingTool',
  mixins: [toolMixin],
  data() {
    return {}
  },
  computed: {},
  watch: {},
  methods: {
    pointing() {
      this.setTool('pointing')
    },
  },

  /* Lifecycles */
  beforeDestroy() {},
  mounted() {},
}
</script>
