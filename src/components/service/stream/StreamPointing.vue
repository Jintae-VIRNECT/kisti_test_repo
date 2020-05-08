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
/* eslint-disable one-var */
import Lottie from 'lottie-web'
import * as animationData from 'assets/json/pointer.lottie.json'

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
// TODO: 좌표값 nomalize된 좌표로 변경 필요함.
export default {
  name: 'Pointing',
  props: {
    scale: Number,
    videoSize: Object,
  },
  data() {
    return {
      radius: '60',
      lottieOption: {
        animationData,
      },
      pointList: [],
      idle: true,
      idleID: 0,
      fullMessage: '',
    }
  },
  computed: {
    // ...mapGetters([
    //   'tools',
    //   'remote'
    // ]),
    pointingColor() {
      return '#0c73f2'
    },
    pointingOpacity() {
      return 1
    },
    // resolution() {
    //     const width = this.remote.opponentScreenWidth || this.$el.offsetWidth;
    //     return width / this.$el.offsetWidth;
    // }
  },
  watch: {
    // 'tools.pointingColor': function (color) {
    //   this.lottieOption.animationData.layers.forEach((layer) => {
    //     layer.shapes[0].it[1].c.k = hexToLottie(color, this.pointingOpacity)
    //     layer.shapes[0].it[1].o.k = this.opacity * 100
    //   })
    // },
    // 'tools.pointingOpacity': function (opacity) {
    //   this.lottieOption.animationData.layers.forEach((layer) => {
    //     layer.shapes[0].it[1].c.k = hexToLottie(this.tools.pointingColor, opacity)
    //     layer.shapes[0].it[1].o.k = this.opacity * 100
    //   })
    // }
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
      // console.log(event);

      this.stateControl()

      this.pointList.push({
        coords: [event.offsetX, event.offsetY],
        color: this.pointingColor,
        opacity: this.pointingOpacity,
        label: 'me!',
      })

      // this.$remoteSDK.message('pointing', {
      //   color: hexToAHEX(this.color, 1),
      //   opacity: this.opacity,
      //   width: this.radius,
      //   posX: (event.offsetX * this.scale).toFixed(2),
      //   posY: (event.offsetY * this.scale).toFixed(2)
      // })
      // console.log({color: hexToAHEX(this.color,1),opacity: this.opacity,width: this.radius,posX: (event.offsetX * this.scale).toFixed(2),posY: (event.offsetY * this.scale).toFixed(2)})

      this.$nextTick(() => {
        const container = this.$el.lastChild
        const lottie = Lottie.loadAnimation({
          ...this.lottieOption,
          container,
        })

        lottie.addEventListener('complete', () => {
          // this.pointList.shift();
          lottie.destroy()
          if (this.idle === true) {
            this.pointList = []
          }
        })
      })
    },
    receivePointing(receive) {
      if (receive.indexOf('{') > -1) {
        this.fullMessage = ''
      }
      if (receive.indexOf('}') < 0) {
        this.fullMessage += receive
        return
      }
      this.fullMessage += receive
      const message = JSON.parse(this.fullMessage)
      this.fullMessage = ''

      if ('type' in message && message.type === 'pointing') {
        this.pointList.push({
          // coords: [(message.posX * this.videoScale), (message.posY * this.videoScale)],
          coords: [message.posX, message.posY],
          color: message.color,
          opacity: message.opacity,
          label: 'opponent',
        })

        this.$nextTick(() => {
          const container = this.$el.lastChild
          const lottie = Lottie.loadAnimation({
            ...this.lottieOption,
            container,
          })

          lottie.addEventListener('complete', () => {
            // this.pointList.shift();
            lottie.destroy()
            if (this.idle === true) {
              this.pointList = []
            }
          })
        })
      }
    },
  },

  /* Lifecycling */
  created() {
    // this.$remoteSDK && this.$remoteSDK.addMessageListener(this.receivePointing)
  },
  mounted() {
    this.lottieOption.animationData.layers.forEach(layer => {
      layer.shapes[0].it[1].c.k = hexToLottie(
        this.pointingColor,
        this.pointingOpacity,
      )
      layer.shapes[0].it[1].o.k = this.pointingOpacity * 100
    })
  },
  beforeDestroy() {
    // this.$remoteSDK && this.$remoteSDK.removeMessageListener(this.receivePointing)
  },
}
</script>
<style lang="scss">
.pointing {
  position: relative;

  &--item {
    position: absolute;
    width: 80px;
    height: 80px;
    margin-top: -40px;
    margin-left: -40px;
    pointer-events: none;
  }
}
</style>
