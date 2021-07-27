<template>
  <tool-button
    :text="$t('button.remove_all')"
    :disabled="!isAvailable"
    :active="status"
    :src="require('assets/image/ic-tool-delete-all.svg')"
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
      if (!this.isAvailable) return
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
    setAvailable(use) {
      this.available = use
    },
  },

  /* Lifecycling */
  created() {
    this.$eventBus.$on(`tool:clearall`, this.setAvailable)
  },
  beforeDestroy() {
    this.$eventBus.$off(`tool:clearall`, this.setAvailable)
  },
}
</script>
