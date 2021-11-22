<template>
  <tool-button
    :text="$t('service.tool_undo')"
    :disabled="disabled"
    :active="status"
    :disableTooltip="disableTooltip"
    :src="require('assets/image/ic-tool-undo.svg')"
    @click.stop="clickHandler"
  ></tool-button>
</template>

<script>
import toolMixin from './toolMixin'
import { VIEW } from 'configs/view.config'

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
      if (this.disabled) return
      this.status = true
      let listener
      if (this.view === VIEW.DRAWING) {
        listener = VIEW.DRAWING
      } else {
        listener = this.viewAction
      }
      this.$eventBus.$emit(`control:${listener}:undo`)
      setTimeout(() => {
        this.status = false
      }, 100)
    },
  },
}
</script>
