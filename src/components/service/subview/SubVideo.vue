<template>
  <div class="sub-video">
    <transition-group
      tag="div"
      class="sub-video__wrapper"
      :class="{ 'no-stream': stream === null }"
      name="opacity"
    >
      <video
        v-if="stream !== null"
        class="sub-video--screen"
        ref="subVideo"
        autoplay
        loop
        muted
        playsinline
        @play="mediaPlay"
        :srcObject.prop="stream"
        key="sub-video"
      ></video>
      <div v-else class="sub-video--no-stream" key="sub-video-no-stream"></div>
      <pano-video
        v-if="activePanoVideo"
        targetRef="subVideo"
        :connectionId="mainView.connectionId"
        key="sub-video-pano"
        type="sub"
      ></pano-video>
    </transition-group>
  </div>
</template>

<script>
import { mapGetters } from 'vuex'
import { CAMERA } from 'configs/device.config'
import { VIEW, ACTION } from 'configs/view.config'

import PanoVideo from 'PanoVideo'

export default {
  name: 'SubVideo',
  components: {
    PanoVideo,
  },
  data() {
    return {
      inited: false,
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
    activePanoVideo() {
      return this.inited && this.stream !== null && this.mainView.streamMode
    },
  },
  mounted() {
    this.$nextTick(() => {
      this.inited = true
    })
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
