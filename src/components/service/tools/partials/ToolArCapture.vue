<template>
  <tool-button
    text="AR 캡쳐"
    :disabled="disabled"
    :active="viewAction === AR_AREA"
    :src="require('assets/image/ic_ar_capture.svg')"
    @click.native="clickHandler"
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
      AR_AREA: ACTION.AR_AREA,
    }
  },
  methods: {
    clickHandler() {
      if (this.callViewMode === VIEW.AR) return
      this.setAction(this.AR_AREA)
    },
  },
}
</script>
