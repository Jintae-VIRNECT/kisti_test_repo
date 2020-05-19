<template>
  <tool-button
    text="포인팅"
    :active="isPointing"
    :src="require('assets/image/ic_pointing.svg')"
    @click="pointing"
  ></tool-button>
</template>

<script>
import toolMixin from './toolMixin'
export default {
  name: 'PointingTool',
  mixins: [toolMixin],
  data() {
    return { isPointing: false }
  },
  computed: {},
  watch: {},
  methods: {
    pointing() {
      this.isPointing = !this.isPointing
      // this.setAction('pointing')
    },
  },

  /* Lifecycles */
  beforeDestroy() {},
  mounted() {},
}
</script>
