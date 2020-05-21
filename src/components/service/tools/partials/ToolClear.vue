<template>
  <tool-button
    text="삭제"
    :src="require('assets/image/ic-tool-delete.svg')"
    @click="clickHandler"
  ></tool-button>
</template>

<script>
import toolMixin from './toolMixin'

export default {
  name: 'ToolClear',
  mixins: [toolMixin],
  data() {
    return {
      mode: '',
    }
  },
  watch: {},
  methods: {
    clickHandler() {},
  },

  /* Lifecycles */
  created() {},
  beforeDestroy() {},
}
</script>
