<template>
  <tool-button
    text="재실행"
    :active="status"
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
  watch: {},
  methods: {
    clickHandler() {
      this.status = true
      // this.$eventBus.$emit(`control:${this.mode}:undo`)
      this.$eventBus.$emit(`control:document:redo`)
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
