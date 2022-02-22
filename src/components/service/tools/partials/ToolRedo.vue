<template>
  <tool-button
    :text="$t('service.tool_redo')"
    :active="status"
    :disabled="disabled"
    :disableTooltip="disableTooltip"
    :src="require('assets/image/ic-tool-redo.svg')"
    @click.stop="clickHandler"
  ></tool-button>
</template>

<script>
import toolMixin from './toolMixin'
import { VIEW } from 'configs/view.config'

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
      if (this.disabled) return
      this.status = true
      let listener
      if (this.view === VIEW.DRAWING) {
        listener = VIEW.DRAWING
      } else {
        listener = this.viewAction
      }
      this.$eventBus.$emit(`control:${listener}:redo`)
      setTimeout(() => {
        this.status = false
      }, 100)
    },
  },
}
</script>
