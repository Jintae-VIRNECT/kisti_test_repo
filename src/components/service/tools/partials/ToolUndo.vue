<template>
  <tool-button
    text="되돌리기"
    :disabled="!isAvailable"
    :active="status"
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
      available: false,
    }
  },
  computed: {
    isAvailable() {
      if (this.disabled) {
        return false
      } else {
        return this.available
      }
    },
  },
  watch: {
    view() {
      this.available = false
    },
  },
  methods: {
    clickHandler() {
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
    setAvailable(use) {
      this.available = use
    },
  },

  /* Lifecycling */
  created() {
    this.$eventBus.$on(`tool:undo`, this.setAvailable)
  },
  beforeDestroy() {
    this.$eventBus.$off(`tool:undo`, this.setAvailable)
  },
}
</script>
