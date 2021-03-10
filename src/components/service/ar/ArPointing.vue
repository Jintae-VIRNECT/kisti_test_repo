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
      let posX = normalizedPosX(event.offsetX, this.videoSize.width)
      let posY = normalizedPosY(event.offsetY, this.videoSize.height)
      if (posX > 1) posX = 1
      if (posY > 1) posY = 1
      const params = {
        color: hexToAHEX(this.pointingColor, 1),
        opacity: 1,
        width: this.radius,
        posX,
        posY,
      }
      this.$call.sendArPointing(AR_POINTING.AR_POINTING, params, [
        this.mainView.connectionId,
      ])
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
      this.$call.sendArPointing(AR_POINTING.UNDO, {}, [
        this.mainView.connectionId,
      ])
    },
    stackRedo() {
      this.$call.sendArPointing(AR_POINTING.REDO, {}, [
        this.mainView.connectionId,
      ])
    },
    drawingClear() {
      this.$call.sendArPointing(AR_POINTING.CLEAR, {}, [
        this.mainView.connectionId,
      ])
    },
  },

  /* Lifecycling */
  created() {
    this.$eventBus.$on(SIGNAL.AR_POINTING, this.receivePointing)
    this.$eventBus.$on(`control:${ACTION.AR_POINTING}:undo`, this.stackUndo)
    this.$eventBus.$on(`control:${ACTION.AR_POINTING}:redo`, this.stackRedo)
    this.$eventBus.$on(`control:${ACTION.AR_POINTING}:clear`, this.drawingClear)
  },
  beforeDestroy() {
    this.$eventBus.$off(SIGNAL.AR_POINTING, this.receivePointing)
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
