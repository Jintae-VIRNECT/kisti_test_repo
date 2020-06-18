<template>
  <div @click="doPointing($event)"></div>
</template>

<script>
import { mapGetters } from 'vuex'
import { reset } from 'utils/callOptions'
import { hexToAHEX } from 'utils/color'
import { ACTION } from 'configs/view.config'
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
      this.$call.arPointing({
        to: this.mainView.id,
        from: this.account.uuid,
        color: hexToAHEX(this.pointingColor, 1),
        opacity: 1,
        width: this.radius,
        posX: normalizedPosX(event.offsetX, this.videoSize.width),
        posY: normalizedPosY(event.offsetY, this.videoSize.height),
      })
    },
  },

  /* Lifecycling */
  mounted() {},
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
