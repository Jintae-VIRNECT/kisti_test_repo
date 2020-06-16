<template>
  <tool-button
    text="텍스트 모드"
    :disabled="disabled"
    :active="viewAction === DRAWING_TEXT"
    :src="require('assets/image/ic-tool-txt.svg')"
    @click.stop="clickHandler"
  ></tool-button>
</template>

<script>
import toolMixin from './toolMixin'
import { ACTION } from 'configs/view.config'

export default {
  name: 'ToolTextMode',
  mixins: [toolMixin],
  data() {
    return {
      DRAWING_TEXT: ACTION.DRAWING_TEXT,
    }
  },
  methods: {
    clickHandler() {
      this.setAction(this.DRAWING_TEXT)
    },
  },
}
</script>
