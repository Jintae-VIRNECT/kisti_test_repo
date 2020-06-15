<template>
  <tool-button
    text="삭제"
    :disabled="disabled"
    :active="status"
    :src="require('assets/image/ic-tool-delete.svg')"
    @click.stop="clickHandler"
  ></tool-button>
</template>

<script>
import toolMixin from './toolMixin'

export default {
  name: 'ToolClear',
  mixins: [toolMixin],
  data() {
    return {
      status: false,
    }
  },
  methods: {
    clickHandler() {
      this.status = true
      this.$eventBus.$emit(`control:${this.view}:clear`)
      setTimeout(() => {
        this.status = false
      }, 100)
    },
  },
}
</script>
