<template>
  <tool-button
    :text="$t('button.remove_all')"
    :disabled="disabled"
    :active="status"
    :disableTooltip="disableTooltip"
    :src="iconImage"
    @click.stop="clickHandler"
  ></tool-button>
</template>

<script>
import toolMixin from './toolMixin'
import { VIEW } from 'configs/view.config'

export default {
  name: 'ToolClear',
  mixins: [toolMixin],
  data() {
    return {
      status: false,
    }
  },
  computed: {
    iconImage() {
      return this.isMobileSize
        ? require('assets/image/call/ic-tool-delete-all_new.svg')
        : require('assets/image/ic-tool-delete-all.svg')
    },
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
      this.$eventBus.$emit(`control:${listener}:clearall`)
      setTimeout(() => {
        this.status = false
      }, 100)
    },
  },
}
</script>
