<template>
  <tool-button
    text="포인팅"
    :active="viewAction === STREAM_POINTING"
    :disabled="disabled"
    :src="require('assets/image/ic_pointing.svg')"
    @click="pointing"
  ></tool-button>
</template>

<script>
import toolMixin from './toolMixin'
import { ACTION } from 'configs/view.config'
export default {
  name: 'PointingTool',
  mixins: [toolMixin],
  data() {
    return {
      STREAM_POINTING: ACTION.STREAM_POINTING,
    }
  },
  methods: {
    pointing() {
      if (this.viewAction !== ACTION.STREAM_POINTING) {
        this.setAction('pointing')
      } else {
        this.setAction('default')
      }
    },
  },
}
</script>
