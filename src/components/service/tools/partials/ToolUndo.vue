<template>
  <tool-button
    text="되돌리기"
    :active="status"
    :src="require('assets/image/ic-tool-undo.svg')"
    @click.stop="clickHandler"
  ></tool-button>
</template>

<script>
import toolMixin from './toolMixin'

export default {
  name: 'ToolUndo',
  mixins: [toolMixin],
  data() {
    return {
      status: false,
    }
  },
  watch: {},
  methods: {
    clickHandler() {
      this.status = true
      // this.$eventBus.$emit(`control:${this.mode}:undo`)
      this.$eventBus.$emit(`control:document:undo`)
      setTimeout(() => {
        this.status = false
      }, 100)
    },
  },

  /* Lifecycles */
  created() {},
  beforeDestroy() {},
}
</script>
