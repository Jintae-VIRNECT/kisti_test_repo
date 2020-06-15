<template>
  <tool-button
    text="재실행"
    :active="status"
    :disabled="disabled"
    :src="require('assets/image/ic-tool-redo.svg')"
    @click.stop="clickHandler"
  ></tool-button>
</template>

<script>
import toolMixin from './toolMixin'

export default {
  name: 'ToolRedo',
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
