<template>
  <tool-button
    text="포인팅"
    :active="action === 'pointing'"
    :src="require('assets/image/call/ic_pointing.png')"
    @click="pointing"
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
      this.setAction('pointing')
    },
  },

  /* Lifecycles */
  beforeDestroy() {},
  mounted() {},
}
</script>
