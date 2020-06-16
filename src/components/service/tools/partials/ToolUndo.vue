<template>
  <tool-button
    text="되돌리기"
    :disabled="disabled"
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
  methods: {
    clickHandler() {
      this.status = true
      this.$eventBus.$emit(`control:${this.view}:undo`)
      setTimeout(() => {
        this.status = false
      }, 100)
    },
  },
}
</script>
