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
import Lottie from 'lottie-web'
import * as animationData from 'assets/json/pointer.lottie.json'
import { mapGetters } from 'vuex'
import { reset } from 'utils/callOptions'
import { hexToAHEX, ahexToHEX } from 'utils/color'
import { SIGNAL } from 'configs/remote.config'
import { ACTION } from 'configs/view.config'
import {
  normalizedPosX,
  normalizedPosY,
  originalPosX,
  originalPosY,
} from 'utils/normalize'

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
  name: 'Pointing',
  props: {
    videoSize: Object,
  },
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
    ...mapGetters(['tools', 'mainView', 'viewAction']),
    pointingColor() {
      return this.tools ? this.tools.color : reset.color
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
      if (this.viewAction !== ACTION.STREAM_POINTING) return
      this.$call.pointing({
        to: this.mainView.id,
        from: this.account.uuid,
        color: hexToAHEX(this.pointingColor, 1),
        opacity: 1,
        width: this.radius,
        posX: normalizedPosX(event.offsetX, this.videoSize.width),
        posY: normalizedPosY(event.offsetY, this.videoSize.height),
      })
    },
    receivePointing(receive) {
      const data = JSON.parse(receive.data)
      // if (data.to !== this.mainView.id) return
      let color = ahexToHEX(data.color)
      this.pointList.push({
        coords: [
          originalPosX(data.posX, this.videoSize.width),
          originalPosY(data.posY, this.videoSize.height),
        ],
        color: color,
        opacity: data.opacity,
        label: 'opponent',
      })
      this.stateControl()

      this.lottieOption.animationData.layers.forEach(layer => {
        layer.shapes[0].it[1].c.k = hexToLottie(color, data.opacity)
        layer.shapes[0].it[1].o.k = data.opacity * 100
      })

      this.$nextTick(() => {
        const container = this.$el.lastChild
        const lottie = Lottie.loadAnimation({
          ...this.lottieOption,
          container,
        })

        lottie.addEventListener('complete', () => {
          lottie.destroy()
          if (this.idle === true) {
            this.pointList.shift()
          }
        })
      })
      return
    },
  },

  /* Lifecycling */
  created() {
    this.$call.addListener(SIGNAL.POINTING, this.receivePointing)
  },
  mounted() {
    this.lottieOption.animationData.layers.forEach(layer => {
      layer.shapes[0].it[1].c.k = hexToLottie(this.pointingColor, 1)
      layer.shapes[0].it[1].o.k = 100
    })
  },
  beforeDestroy() {
    this.$call.removeListener(SIGNAL.POINTING, this.receivePointing)
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
