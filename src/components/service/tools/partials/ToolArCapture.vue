<template>
  <tool-button
    :text="$t('service.ar_capute')"
    :disabled="disabled"
    :active="active"
    :disableTooltip="disableTooltip"
    :src="require('assets/image/ic_ar_capture.svg')"
    @click.native="clickHandler"
  ></tool-button>
</template>

<script>
import toolMixin from './toolMixin'
import { VIEW, ACTION } from 'configs/view.config'

export default {
  name: 'ToolArCapture',
  mixins: [toolMixin],
  computed: {
    active() {
      return this.isMobileSize ? false : this.viewAction === ACTION.AR_AREA
    },
  },
  methods: {
    clickHandler() {
      if (this.disabled) return
      if (this.view !== VIEW.AR) return

      this.setAction(ACTION.AR_AREA)
    },
  },
}
</script>
