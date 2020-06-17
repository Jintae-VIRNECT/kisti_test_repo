<template>
  <div class="main-video">
    <div
      class="main-video__box"
      @mouseenter="showTools = true"
      @mouseleave="showTools = false"
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

        <pointing :scale="1" class="main-video__pointing"></pointing>
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
        <div class="main-video__empty-inner" v-else-if="false">
          <img src="~assets/image/img_video_stop.svg" />
          <p>영상을 정지하였습니다.</p>
          <p class="inner-discription" v-if="true">
            작업자의 Remote App이<br />백그라운드 상태입니다.
          </p>
          <p class="inner-discription" v-else>
            작업자의 영상이 일시정지 상태입니다.
          </p>
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
    <capture-modal
      v-if="imgBlob"
      :imgUrl="imgBlob"
      @close="imgBlob = ''"
      @recapture="doCapture"
    ></capture-modal>
  </div>
</template>

<script>
import { mapActions, mapGetters } from 'vuex'
import { ACTION } from 'configs/view.config'

import Pointing from './StreamPointing'
import VideoTools from './MainVideoTools'
import CaptureModal from '../modal/CaptureModal'
export default {
  name: 'MainVideo',
  components: {
    Pointing,
    VideoTools,
    CaptureModal,
  },
  data() {
    return {
      status: 'good', // good, normal, bad
      showTools: false,
      loaded: false,
      STREAM_POINTING: ACTION.STREAM_POINTING,
      imgBlob: '',
    }
  },
  computed: {
    ...mapGetters({
      mainView: 'mainView',
      speaker: 'speaker',
      viewAction: 'viewAction',
      resolutions: 'resolutions',
      initing: 'initing',
    }),
    resolution() {
      const idx = this.resolutions.findIndex(
        data => data.connectionId === this.mainView.connectionId,
      )
      if (idx < 0 || this.resolutions.width * this.resolutions.height === 0) {
        return {
          width: 0,
          height: 0,
        }
      }
      return this.resolutions[idx]
    },
  },
  watch: {
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
    ...mapActions(['updateAccount']),
    mediaPlay() {
      if (this.mainView.me && this.mainView.stream) {
        const videoEl = this.$el.querySelector('#main-video')
        this.$call.sendResolution({
          width: videoEl.offsetWidth,
          height: videoEl.offsetHeight,
        })
      }
      this.loaded = true
      this.optimizeVideoSize()
    },
    optimizeVideoSize() {
      console.log('OPTIMIZE VIDEO!!!!')
      const mainWrapper = this.$el
      const videoBox = this.$el.querySelector('.main-video__box')
      if (this.resolution.width === 0 || this.resolution.height === 0) return

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
      } else {
        // width에 맞춤
        videoBox.style.height = maxWidth / scale + 'px'
        videoBox.style.width = maxWidth + 'px'
      }
    },
    doCapture() {
      const videoEl = this.$el.querySelector('#main-video')

      const width = videoEl.offsetWidth
      const height = videoEl.offsetHeight

      const tmpCanvas = document.createElement('canvas')
      tmpCanvas.width = width
      tmpCanvas.height = height

      const tmpCtx = tmpCanvas.getContext('2d')

      tmpCtx.drawImage(this.$refs['mainVideo'], 0, 0, width, height)

      tmpCanvas.toBlob(blob => {
        this.imgBlob = URL.createObjectURL(blob)
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
