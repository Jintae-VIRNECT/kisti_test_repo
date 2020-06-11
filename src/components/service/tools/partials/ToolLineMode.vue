<template>
  <tool-button
    text="그리기 모드"
    :active="action === 'line'"
    :src="require('assets/image/ic-tool-draw.svg')"
    @click.stop="clickHandler"
  ></tool-button>
</template>

<script>
import toolMixin from './toolMixin'

export default {
  name: 'ToolLineMode',
  mixins: [toolMixin],
  data() {
    return {
      mode: '',
      status: false,
    }
  },
  watch: {
    callViewMode(viewMode) {
      this.$eventBus.$off(`control:${this.mode}:mode`, this.changeStatus)
      this.$eventBus.$on(`control:${viewMode}:mode`, this.changeStatus)

      this.mode = viewMode
      if (viewMode === 'ar') {
        this.status = true
        this.$eventBus.$emit(`control:${this.callViewMode}:mode`, 'line')
      } else {
        this.status = false
        this.clickHandler()
      }
    },
  },
  methods: {
    clickHandler() {
      if (this.callViewMode === 'ar') return

      this.status = !this.status
      this.$eventBus.$emit(
        `control:document:mode`,
        this.status ? 'line' : false,
      )
      if (!!this.status === true) {
        this.setAction('line')
      } else {
        this.setAction('')
      }
    },
    changeStatus(mode) {
      this.status = mode === 'line'
    },
  },

  /* Lifecycles */
  created() {
    this.mode = this.callViewMode
    this.$eventBus.$on(`control:${this.mode}:mode`, this.changeStatus)
    if (this.callViewMode === 'ar') {
      this.status = true
      this.$eventBus.$emit(`control:${this.callViewMode}:mode`, 'line')
    } else {
      this.clickHandler()
    }
  },
  beforeDestroy() {
    this.$eventBus.$off(`control:${this.mode}:mode`, this.changeStatus)
  },
}
</script>
