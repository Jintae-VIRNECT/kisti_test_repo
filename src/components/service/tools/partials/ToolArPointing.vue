<template>
  <tool-button
    :text="$t('service.ar_pointing')"
    :disabled="disabled"
    :disableTooltip="disableTooltip"
    :active="active"
    :src="require('assets/image/ic_ar_pointing.svg')"
    @click.native="clickHandler"
  ></tool-button>
</template>

<script>
import toolMixin from './toolMixin'
import { ACTION } from 'configs/view.config'

export default {
  name: 'ToolLineMode',
  mixins: [toolMixin],
  data() {
    return {
      AR_POINTING: ACTION.AR_POINTING,
    }
  },
  computed: {
    active() {
      return this.isMobileSize ? false : this.viewAction === this.AR_POINTING
    },
  },
  methods: {
    clickHandler() {
      if (this.disabled) return
      this.setAction(this.AR_POINTING)
    },
  },
}
</script>
