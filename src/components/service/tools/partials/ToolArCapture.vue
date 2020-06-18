<template>
  <tool-button
    text="AR 캡쳐"
    :disabled="disabled"
    :active="active"
    :src="require('assets/image/ic_ar_capture.svg')"
    @click.native="clickHandler"
  ></tool-button>
</template>

<script>
import toolMixin from './toolMixin'
import { VIEW, ACTION } from 'configs/view.config'
import { AR_DRAWING } from 'configs/remote.config'

export default {
  name: 'ToolLineMode',
  mixins: [toolMixin],
  computed: {
    active() {
      if (this.viewAction === ACTION.AR_AREA) {
        return true
      } else {
        return false
      }
    },
  },
  watch: {
    viewAction(val) {
      if (ACTION.AR_AREA !== val) {
        this.$call.arDrawing(AR_DRAWING.END_DRAWING)
      }
    },
  },
  methods: {
    clickHandler() {
      if (this.view !== VIEW.AR) return

      this.$call.arDrawing(AR_DRAWING.START_DRAWING)
      this.setAction(ACTION.AR_AREA)
    },
  },
}
</script>
