<template>
  <tool-button
    text="재실행"
    :src="require('assets/image/ic-tool-redo.svg')"
    @action="clickHandler"
  ></tool-button>
</template>

<script>
import toolMixin from './toolMixin'

export default {
  name: 'ToolRedo',
  mixins: [toolMixin],
  data() {
    return {
      mode: '',
      status: false,
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
