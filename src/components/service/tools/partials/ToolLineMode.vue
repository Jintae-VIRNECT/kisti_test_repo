<template>
  <tool-button
    text="그리기 모드"
    :disabled="disabled"
    :active="viewAction === DRAWING_LINE || viewAction === AR_DRAWING"
    :src="require('assets/image/ic-tool-draw.svg')"
    @click.stop="clickHandler"
  ></tool-button>
</template>

<script>
import toolMixin from './toolMixin'
import { VIEW, ACTION } from 'configs/view.config'

export default {
  name: 'ToolLineMode',
  mixins: [toolMixin],
  data() {
    return {
      mode: '',
      status: false,
      DRAWING_LINE: ACTION.DRAWING_LINE,
      AR_DRAWING: ACTION.AR_DRAWING,
    }
  },
  methods: {
    clickHandler() {
      if (this.view === VIEW.AR) return

      this.setAction(this.DRAWING_LINE)
    },
  },
}
</script>
