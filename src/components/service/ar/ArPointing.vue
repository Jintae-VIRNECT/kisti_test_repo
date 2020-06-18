<template>
  <div @click="doPointing($event)">
    <div
      v-for="(point, index) in pointList"
      :key="index"
      class="pointing--item"
      :style="{ left: point.coords[0] + 'px', top: point.coords[1] + 'px' }"
    />
  </div>
</template>

<script>
import * as animationData from 'assets/json/pointer.lottie.json'
import { mapGetters } from 'vuex'
import { reset } from 'utils/callOptions'
import { hexToAHEX } from 'utils/color'
import { ACTION } from 'configs/view.config'
import { normalizedPosX, normalizedPosY } from 'utils/normalize'
import toastMixin from 'mixins/toast'

function hexToLottie(hex, alpha) {
  var r = parseInt(hex.slice(1, 3), 16) / 255,
    g = parseInt(hex.slice(3, 5), 16) / 255,
    b = parseInt(hex.slice(5, 7), 16) / 255

  if (alpha) {
    if (alpha > 1) {
      alpha /= 100
    }

    return [r, g, b, alpha]
  } else {
    return [r, g, b]
  }
}

export default {
  name: 'ARPointing',
  mixins: [toastMixin],
  data() {
    return {
      radius: '60',
      lottieOption: {
        animationData,
        loop: false,
      },
      pointList: [],
      idle: true,
      idleID: 0,
      fullMessage: '',
    }
  },
  computed: {
    ...mapGetters(['tools', 'mainView', 'viewAction', 'resolutions']),
    pointingColor() {
      return this.tools ? this.tools.color : reset.color
    },
    resolution() {
      const idx = this.resolutions.findIndex(
        data => data.connectionId === this.mainView.connectionId,
      )
      if (idx < 0) {
        return {
          width: 0,
          height: 0,
        }
      }
      return this.resolutions[idx]
    },
  },
  methods: {
    stateControl() {
      this.idle = false
      clearTimeout(this.idleID)
      this.idleID = setTimeout(() => {
        this.idle = true
      }, 1050)
    },
    doPointing(event) {
      if (this.viewAction !== ACTION.AR_POINTING) return
      this.$call.arPointing({
        to: this.mainView.id,
        from: this.account.uuid,
        color: hexToAHEX(this.pointingColor, 1),
        opacity: 1,
        width: this.radius,
        posX: normalizedPosX(event.offsetX, this.resolution.width),
        posY: normalizedPosY(event.offsetY, this.resolution.height),
      })
    },
  },

  /* Lifecycling */
  mounted() {
    this.lottieOption.animationData.layers.forEach(layer => {
      layer.shapes[0].it[1].c.k = hexToLottie(this.pointingColor, 1)
      layer.shapes[0].it[1].o.k = 100
    })
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
