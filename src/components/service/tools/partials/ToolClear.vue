<template>
  <tool-button
    text="삭제"
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
  watch: {},
  methods: {
    clickHandler() {
      this.status = true
      // this.$eventBus.$emit(`control:${this.mode}:clear`)
      this.$eventBus.$emit(`control:document:clear`)
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
