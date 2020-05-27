<template>
  <tool-button
    text="텍스트 모드"
    :active="action === 'text'"
    :src="require('assets/image/ic-tool-txt.svg')"
    @action="clickHandler"
  ></tool-button>
</template>

<script>
import toolMixin from './toolMixin'

export default {
  name: 'ToolTextMode',
  mixins: [toolMixin],
  data() {
    return {}
  },
  methods: {
    clickHandler() {
      this.status = !this.status
      this.$eventBus.$emit(
        `control:${this.callViewMode}:mode`,
        this.status ? 'text' : false,
      )
      if (!!this.status === true) {
        this.setAction('text')
      }
    },
    changeStatus(mode) {
      this.status = mode === 'text'
    },
  },

  /* Lifecycles */
  created() {
    this.mode = this.callViewMode
    this.$eventBus.$on(`control:${this.mode}:mode`, this.changeStatus)
  },
  beforeDestroy() {
    this.$eventBus.$off(`control:${this.mode}:mode`, this.changeStatus)
  },
}
</script>
