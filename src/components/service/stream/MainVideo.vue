<template>
  <div class="main-video">
    <div
      class="main-video__box"
      @mouseenter="hoverTools = true"
      @mouseleave="hoverTools = false"
      :class="{ shutter: showShutter }"
    >
      <!-- 메인 비디오 뷰 -->
      <video
        ref="mainVideo"
        id="main-video"
        :srcObject.prop="mainView.stream"
        @play="mediaPlay"
        @loadeddata="optimizeVideoSize"
        :muted="!speaker || mainView.id === account.uuid"
        autoplay
        playsinline
        loop
      ></video>
      <template v-if="loaded">
        <!-- 전체공유 표출 -->
        <transition name="opacity">
          <div class="main-video__sharing" v-if="viewForce">
            <button
              v-if="isLeader"
              class="btn small main-video__sharing-button active"
              @click="cancelSharing"
            >
              {{ $t('button.stream_sharing_cancel') }}
            </button>
            <button v-else class="btn small main-video__sharing-button">
              {{ $t('button.stream_sharing') }}
            </button>
          </div>
        </transition>

        <!-- 녹화 시간 정보 -->
        <div class="main-video__recording">
          <div class="main-video__recording--time" v-if="serverTimer">
            <p class="server">
              {{ serverTime | timeFilter }}
            </p>
          </div>
          <div class="main-video__recording--time" v-if="localTimer">
            <p class="local">{{ localTime | timeFilter }}</p>
          </div>
        </div>

        <!-- 포인팅 -->
        <pointing
          v-if="viewForce"
          :videoSize="videoSize"
          class="main-video__pointing"
        ></pointing>
        <!-- 디바이스 컨트롤 뷰 -->
        <template v-if="allowTools">
          <transition name="opacity">
            <video-tools v-if="hoverTools"></video-tools>
          </transition>
        </template>
      </template>
    </div>
    <div class="main-video__empty" v-if="!loaded">
      <transition name="opacity">
        <!-- 영상 연결중 -->
        <!-- <div class="main-video__empty-inner" v-if="resolutions.length > 0">
          <img src="~assets/image/img_video_connecting.svg" />
          <p>{{ $t('service.stream_connecting') }}</p>
        </div> -->
        <!-- 영상이 없을 경우 -->
        <div class="main-video__empty-inner" v-if="resolutions.length === 0">
          <img src="~assets/image/img_novideo.svg" />
          <p>{{ $t('service.stream_no_video') }}</p>
          <p class="inner-discription">
            {{ $t('service.stream_no_worker') }}
          </p>
        </div>
        <div class="main-video__empty-inner" v-else>
          <img src="~assets/image/call/img_select_video.svg" />
          <p v-html="$t('service.stream_choice')"></p>
        </div>
      </transition>
      <!-- 영상 초기화 로딩 -->
      <transition name="opacity">
        <div class="main-video__empty-inner loading" v-if="initing">
          <img src="~assets/image/gif_loading.svg" />
        </div>
      </transition>
    </div>
    <transition name="opacity">
      <div
        class="main-video__empty"
        v-if="
          (loaded && cameraStatus === 'off') || cameraStatus === 'background'
        "
      >
        <transition name="opacity">
          <!-- 영상 백그라운드 및 정지 표출 -->
          <div class="main-video__empty-inner" v-if="mainView.me !== true">
            <img src="~assets/image/img_video_stop.svg" />
            <p>{{ $t('service.stream_stop') }}</p>
            <p
              class="inner-discription"
              v-if="cameraStatus === 'background'"
              v-html="$t('service.stream_background')"
            ></p>
            <p class="inner-discription" v-else>
              {{ $t('service.stream_stoped') }}
            </p>
          </div>
          <div class="main-video__empty-inner" v-else>
            <img src="~assets/image/img_novideo.svg" />
            <p>{{ $t('service.stream_off') }}</p>
          </div>
        </transition>
      </div>
    </transition>
  </div>
</template>

<script>
import { mapActions, mapGetters } from 'vuex'
import { ROLE } from 'configs/remote.config'
import { ACTION } from 'configs/view.config'
import { CAMERA, FLASH } from 'configs/device.config'

import Pointing from './StreamPointing'
import VideoTools from './MainVideoTools'
import shutterMixin from 'mixins/shutter'
import toastMixin from 'mixins/toast'
export default {
  name: 'MainVideo',
  mixins: [shutterMixin, toastMixin],
  components: {
    Pointing,
    VideoTools,
  },
  data() {
    return {
      status: 'good', // good, normal, bad
      hoverTools: false,
      loaded: false,
      videoSize: {
        width: 0,
        height: 0,
      },

      localTimer: null,
      localTime: 0,
      localStart: 0,
      serverTimer: null,
      serverTime: 0,
      serverStart: 0,
    }
  },
  computed: {
    ...mapGetters({
      mainView: 'mainView',
      speaker: 'speaker',
      viewAction: 'viewAction',
      resolutions: 'resolutions',
      initing: 'initing',
      viewForce: 'viewForce',
    }),
    isLeader() {
      if (this.account.roleType === ROLE.LEADER) {
        return true
      } else {
        return false
      }
    },
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
        if (this.mainView.cameraStatus === CAMERA.CAMERA_OFF) {
          return 'off'
        } else if (this.mainView.cameraStatus === CAMERA.APP_IS_BACKGROUND) {
          return 'background'
        }
        return 'on'
      } else {
        return -1
      }
    },
    allowTools() {
      if (
        this.viewForce === true &&
        this.account.roleType === ROLE.LEADER &&
        this.viewAction !== ACTION.STREAM_POINTING
      ) {
        if (
          this.mainView.flash === FLASH.FLASH_NONE &&
          this.mainView.zoomMax === 1
        ) {
          return false
        }
        return true
      } else {
        return false
      }
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
    viewForce(flag, oldFlag) {
      if (!this.isLeader) {
        if (flag === false && oldFlag === true) {
          this.addChat({
            name: this.mainView.nickname,
            status: 'sharing-stop',
            type: 'system',
          })
        }
        if (flag === true && oldFlag === false) {
          this.$nextTick(() => {
            this.addChat({
              name: this.mainView.nickname,
              status: 'sharing-start',
              type: 'system',
            })
          })
        }
      }
    },
    cameraStatus(status, oldStatus) {
      if (status === oldStatus || oldStatus === -1) return
      if (!this.mainView || !this.mainView.id) return
      if (status === 'off') {
        if (oldStatus === 'background') return
        this.addChat({
          name: this.mainView.nickname,
          status: 'stream-stop',
          type: 'system',
        })
      } else if (status === 'background') {
        this.addChat({
          name: this.mainView.nickname,
          status: 'stream-background',
          type: 'system',
        })
      } else if (status === 'on') {
        this.addChat({
          name: this.mainView.nickname,
          status: 'stream-start',
          type: 'system',
        })
      }
    },
  },
  methods: {
    ...mapActions(['updateAccount', 'setCapture', 'addChat', 'setMainView']),
    cancelSharing() {
      this.addChat({
        name: this.mainView.nickname,
        status: 'sharing-stop',
        type: 'system',
      })
      this.setMainView({ force: false })
      this.$call.mainview(this.mainView.id, false)
    },
    mediaPlay() {
      this.$nextTick(() => {
        this.optimizeVideoSize()
        this.loaded = true
      })
    },
    optimizeVideoSize() {
      const mainWrapper = this.$el
      const videoBox = this.$el.querySelector('.main-video__box')
      const video = this.$refs['mainVideo']
      if (this.resolution.width === 0 || this.resolution.height === 0) return

      this.debug(
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
        // video.style.height = maxHeight + 'px'
        // video.style.width = maxHeight * scale + 'px'
      } else {
        // width에 맞춤
        videoBox.style.height = maxWidth / scale + 'px'
        videoBox.style.width = maxWidth + 'px'
        // video.style.height = maxWidth / scale + 'px'
        // video.style.width = maxWidth + 'px'
      }
      this.videoSize.width = video.offsetWidth
      this.videoSize.height = video.offsetHeight
      this.debug('calc size: ', this.videoSize.width, this.videoSize.height)
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
        const imgId = parseInt(
          Date.now()
            .toString()
            .substr(-9),
        )
        this.setCapture({
          id: imgId,
          fileData: blob,
          fileName: `Remote_Capture_${this.$dayjs().format(
            'YYMMDD_HHmmss',
          )}.png`,
          width,
          height,
        })
      }, 'image/png')
    },
    localRecord(isStart) {
      if (isStart) {
        this.localStart = this.$dayjs().unix()
        this.localTimer = setInterval(() => {
          const diff = this.$dayjs().unix() - this.localStart

          this.localTime = this.$dayjs
            .duration(diff, 'seconds')
            .as('milliseconds')
        }, 1000)
      } else {
        clearInterval(this.localTimer)
        this.localTime = 0
        this.localTimer = null
      }
    },
    serverRecord(isStart) {
      if (isStart) {
        this.serverStart = this.$dayjs().unix()
        this.serverTimer = setInterval(() => {
          const diff = this.$dayjs().unix() - this.serverStart

          this.serverTime = this.$dayjs
            .duration(diff, 'seconds')
            .as('milliseconds')
        }, 1000)
      } else {
        clearInterval(this.serverTimer)
        this.serverTime = 0
        this.serverTimer = null
      }
    },
  },

  /* Lifecycles */
  beforeDestroy() {
    this.$eventBus.$off('capture', this.doCapture)
    this.$eventBus.$off('localRecord', this.localRecord)
    this.$eventBus.$off('serverRecord', this.serverRecord)
    window.removeEventListener('resize', this.optimizeVideoSize)
  },
  created() {
    this.$eventBus.$on('capture', this.doCapture)
    this.$eventBus.$on('localRecord', this.localRecord)
    this.$eventBus.$on('serverRecord', this.serverRecord)
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
