<template>
  <div class="main-video">
    <div
      class="main-video__box"
      @mouseenter="showTools = true"
      @mouseleave="showTools = false"
      :class="{ shutter: false }"
    >
      <video
        ref="mainVideo"
        id="main-video"
        :srcObject.prop="mainView.stream"
        @play="mediaPlay"
        :muted="!speaker"
        autoplay
        playsinline
        loop
      ></video>
      <template v-if="loaded">
        <div class="main-video__recording" v-if="false">
          <p class="server">{{ 0 | timeFilter }}</p>
          <p class="local">{{ 0 | timeFilter }}</p>
        </div>

        <pointing
          :videoSize="videoSize"
          class="main-video__pointing"
        ></pointing>
        <template v-if="viewAction !== STREAM_POINTING">
          <transition name="opacity">
            <video-tools v-if="showTools"></video-tools>
          </transition>
        </template>
      </template>
    </div>
    <div class="main-video__empty" v-if="!loaded">
      <transition name="opacity">
        <div class="main-video__empty-inner" v-if="resolutions.length > 0">
          <img src="~assets/image/img_video_connecting.svg" />
          <p>영상 연결 중…</p>
        </div>
        <div class="main-video__empty-inner" v-else>
          <img src="~assets/image/img_novideo.svg" />
          <p>출력 할 영상이 없습니다.</p>
          <p class="inner-discription">
            접속중인 작업자가 없습니다.
          </p>
        </div>
      </transition>
      <transition name="opacity">
        <div class="main-video__empty-inner loading" v-if="initing">
          <img src="~assets/image/gif_loading.svg" />
        </div>
      </transition>
    </div>
    <transition name="opacity">
      <div class="main-video__empty" v-if="loaded && cameraStatus !== -1">
        <transition name="opacity">
          <div class="main-video__empty-inner" v-if="cameraStatus === 'off'">
            <img src="~assets/image/img_video_stop.svg" />
            <p>영상을 정지하였습니다.</p>
            <p class="inner-discription" v-if="cameraStatus === 'background'">
              작업자의 Remote App이<br />백그라운드 상태입니다.
            </p>
            <p class="inner-discription" v-else>
              작업자의 영상이 일시정지 상태입니다.
            </p>
          </div>
        </transition>
      </div>
    </transition>
  </div>
</template>

<script>
import { mapActions, mapGetters } from 'vuex'
import { ACTION } from 'configs/view.config'
import { CAMERA } from 'configs/device.config'

import Pointing from './StreamPointing'
import VideoTools from './MainVideoTools'
import shutterMixin from 'mixins/shutter'
export default {
  name: 'MainVideo',
  mixins: [shutterMixin],
  components: {
    Pointing,
    VideoTools,
  },
  data() {
    return {
      status: 'good', // good, normal, bad
      showTools: false,
      loaded: false,
      STREAM_POINTING: ACTION.STREAM_POINTING,
      videoSize: {
        width: 0,
        height: 0,
      },
    }
  },
  computed: {
    ...mapGetters({
      mainView: 'mainView',
      speaker: 'speaker',
      viewAction: 'viewAction',
      resolutions: 'resolutions',
      initing: 'initing',
      deviceInfo: 'deviceInfo',
    }),
    resolution() {
      const idx = this.resolutions.findIndex(
        data => data.connectionId === this.mainView.connectionId,
      )
      if (
        idx < 0 ||
        this.resolutions[idx].width * this.resolutions[idx].height === 0
      ) {
        return {
          width: 0,
          height: 0,
        }
      }
      return this.resolutions[idx]
    },
    cameraStatus() {
      if (this.mainView && this.mainView.id) {
        if (this.deviceInfo.cameraStatus === CAMERA.CAMERA_OFF) {
          return 'off'
        } else if (this.deviceInfo.cameraStatus === CAMERA.APP_IS_BACKGROUND) {
          return 'background'
        }
        return -1
      } else {
        return -1
      }
    },
  },
  watch: {
    deviceInfo: {
      deep: true,
      handler(e) {
        console.log(e)
      },
    },
    cameraStatus(val) {
      console.log('camera status change:', val)
    },
    speaker(val) {
      this.$refs['mainVideo'].muted = val ? false : true
    },
    resolution: {
      deep: true,
      handler() {
        this.optimizeVideoSize()
      },
    },
    mainView: {
      deep: true,
      handler(view) {
        if (!view.id) {
          this.loaded = false
          const videoBox = this.$el.querySelector('.main-video__box')
          videoBox.style.height = '100%'
          videoBox.style.width = '100%'
        }
      },
    },
  },
  methods: {
    ...mapActions(['updateAccount', 'setCapture']),
    mediaPlay() {
      if (this.mainView.me && this.mainView.stream) {
        const videoEl = this.$el.querySelector('#main-video')
        this.$call.sendResolution({
          width: videoEl.offsetWidth,
          height: videoEl.offsetHeight,
          orientation: '',
        })
      }
      this.loaded = true
      this.optimizeVideoSize()
    },
    optimizeVideoSize() {
      const mainWrapper = this.$el
      const videoBox = this.$el.querySelector('.main-video__box')
      const video = this.$refs['mainVideo']
      if (this.resolution.width === 0 || this.resolution.height === 0) return

      console.log(
        'current resolution: ',
        this.resolution.width,
        this.resolution.height,
      )

      let maxWidth = mainWrapper.offsetWidth
      let maxHeight = mainWrapper.offsetHeight
      let scale = this.resolution.width / this.resolution.height
      if (
        this.resolution.width / this.resolution.height <
        maxWidth / maxHeight
      ) {
        // height에 맞춤
        videoBox.style.height = maxHeight + 'px'
        videoBox.style.width = maxHeight * scale + 'px'
        video.style.height = maxHeight + 'px'
        video.style.width = maxHeight * scale + 'px'
      } else {
        // width에 맞춤
        videoBox.style.height = maxWidth / scale + 'px'
        videoBox.style.width = maxWidth + 'px'
        video.style.height = maxWidth / scale + 'px'
        video.style.width = maxWidth + 'px'
      }
      this.videoSize.width = video.offsetWidth
      this.videoSize.height = video.offsetHeight
      console.log('calc size: ', this.videoSize.width, this.videoSize.height)
    },
    doCapture() {
      const videoEl = this.$refs['mainVideo']

      const width = videoEl.offsetWidth
      const height = videoEl.offsetHeight

      const tmpCanvas = document.createElement('canvas')
      tmpCanvas.width = width
      tmpCanvas.height = height

      const tmpCtx = tmpCanvas.getContext('2d')

      tmpCtx.drawImage(videoEl, 0, 0, width, height)

      tmpCanvas.toBlob(blob => {
        this.setCapture({
          id: Date.now(),
          img: URL.createObjectURL(blob),
          fileName: `Remote_Capture_${this.$dayjs().format(
            'YYMMDD_HHmmss',
          )}.png`,
        })
      }, 'image/png')
    },
  },

  /* Lifecycles */
  beforeDestroy() {
    this.$eventBus.$off('capture', this.doCapture)
    window.removeEventListener('resize', this.optimizeVideoSize)
  },
  created() {
    this.$eventBus.$on('capture', this.doCapture)
    window.addEventListener('resize', this.optimizeVideoSize)
  },
}
</script>

<style>
.opacity-enter-active,
.opacity-leave-active {
  transition: opacity ease 0.4s;
}
.opacity-enter {
  opacity: 0;
}
.opacity-enter-to {
  opacity: 1;
}
.opacity-leave {
  opacity: 1;
}
.opacity-leave-to {
  opacity: 0;
}
</style>
