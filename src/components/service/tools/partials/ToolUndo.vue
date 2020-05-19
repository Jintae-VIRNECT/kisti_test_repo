<template>
  <tool-button
    text="되돌리기"
    :src="require('assets/image/ic-tool-undo.svg')"
    @action="clickHandler"
  ></tool-button>
</template>

<script>
import toolMixin from './toolMixin'

export default {
  name: 'ToolUndo',
  mixins: [toolMixin],
  data() {
    return {}
  },
  watch: {},
  methods: {
    clickHandler() {},
  },

  /* Lifecycles */
  created() {},
  beforeDestroy() {},
}
</script>
