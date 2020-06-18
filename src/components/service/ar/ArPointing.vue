<template>
  <div @click="doPointing($event)"></div>
</template>

<script>
import { mapGetters } from 'vuex'
import { reset } from 'utils/callOptions'
import { hexToAHEX } from 'utils/color'
import { ACTION } from 'configs/view.config'
import { SIGNAL, AR_POINTING } from 'configs/remote.config'
import { normalizedPosX, normalizedPosY } from 'utils/normalize'
import toastMixin from 'mixins/toast'

export default {
  name: 'ARPointing',
  mixins: [toastMixin],
  props: {
    videoSize: Object,
  },
  data() {
    return {
      radius: '60',
    }
  },
  computed: {
    ...mapGetters(['tools', 'mainView', 'viewAction']),
    pointingColor() {
      return this.tools ? this.tools.color : reset.color
    },
  },
  methods: {
    doPointing(event) {
      if (this.viewAction !== ACTION.AR_POINTING) return
      const params = {
        to: this.mainView.id,
        from: this.account.uuid,
        color: hexToAHEX(this.pointingColor, 1),
        opacity: 1,
        width: this.radius,
        posX: normalizedPosX(event.offsetX, this.videoSize.width),
        posY: normalizedPosY(event.offsetY, this.videoSize.height),
      }
      this.$call.arPointing(AR_POINTING.AR_POINTING, params)
    },
    receivePointing(receive) {
      const data = JSON.parse(receive.data)
      if (data.from === this.account.uuid) return

      if (data.type === AR_POINTING.UNDO_ABLE) {
        this.$eventBus.$emit(`tool:undo`, data.isAvailable)
      }
      if (data.type === AR_POINTING.REDO_ABLE) {
        this.$eventBus.$emit(`tool:redo`, data.isAvailable)
      }
      if (data.type === AR_POINTING.CLEAR_ABLE) {
        this.$eventBus.$emit(`tool:clear`, data.isAvailable)
      }
    },
    stackUndo() {
      this.$call.arPointing(AR_POINTING.UNDO)
    },
    stackRedo() {
      this.$call.arPointing(AR_POINTING.REDO)
    },
    drawingClear() {
      this.$call.arPointing(AR_POINTING.CLEAR)
    },
  },

  /* Lifecycling */
  created() {
    this.$call.addListener(SIGNAL.AR_POINTING, this.receivePointing)
    this.$eventBus.$on(`control:${ACTION.AR_POINTING}:undo`, this.stackUndo)
    this.$eventBus.$on(`control:${ACTION.AR_POINTING}:redo`, this.stackRedo)
    this.$eventBus.$on(`control:${ACTION.AR_POINTING}:clear`, this.drawingClear)
  },
  beforeDestroy() {
    this.$eventBus.$off(`control:${ACTION.AR_POINTING}:undo`, this.stackUndo)
    this.$eventBus.$off(`control:${ACTION.AR_POINTING}:redo`, this.stackRedo)
    this.$eventBus.$off(
      `control:${ACTION.AR_POINTING}:clear`,
      this.drawingClear,
    )
  },
}
</script>
<style lang="scss">
.pointing {
  position: relative;
}
.pointing--item {
  position: absolute;
  width: 80px;
  height: 80px;
  margin-top: -40px;
  margin-left: -40px;
  pointer-events: none;
}
</style>
