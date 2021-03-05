<template>
  <div class="sub-video">
    <transition name="opacity">
      <video
        class="sub-video--screen"
        v-if="stream !== null"
        ref="subVideo"
        autoplay
        loop
        muted
        playsinline
        @play="mediaPlay"
        :srcObject.prop="stream"
      ></video>
      <div v-else class="sub-video--no-stream"></div>
    </transition>
  </div>
</template>

<script>
import { mapGetters } from 'vuex'
import { CAMERA } from 'configs/device.config'
import { VIEW, ACTION } from 'configs/view.config'
export default {
  name: 'SubVideo',
  data() {
    return {
      backInterval: null,
    }
  },
  computed: {
    ...mapGetters(['mainView', 'view', 'viewAction']),
    stream() {
      if (this.mainView && this.mainView.id) {
        if (this.view === VIEW.AR && this.viewAction !== ACTION.AR_DRAWING) {
          return null
        } else {
          if (
            this.mainView.cameraStatus === CAMERA.CAMERA_ON ||
            this.mainView.cameraStatus === 'default'
          ) {
            return this.mainView.stream
          } else {
            return null
          }
        }
      } else {
        return null
      }
    },
  },
  methods: {
    mediaPlay() {
      this.$nextTick(() => {
        if (this.isSafari && this.isTablet) {
          this.checkBackgroundStream()
        }
      })
    },
    checkBackgroundStream() {
      if (this.backInterval) clearInterval(this.backInterval)
      let lastFired = new Date().getTime()
      let now = 0
      this.backInterval = setInterval(() => {
        now = new Date().getTime()
        if (now - lastFired > 1000) {
          this.$refs['subVideo'].play()
        }
        lastFired = now
      }, 500)
    },
  },
  beforeDestroy() {
    if (this.backInterval) clearInterval(this.backInterval)
  },
}
</script>
