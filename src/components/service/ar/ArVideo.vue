<template>
  <div class="ar-video">
    <div class="ar-video__box">
      <video
        class="ar-video__stream"
        ref="arVideo"
        :srcObject.prop="mainView.stream"
        @play="mediaPlay"
        @loadeddata="mediaPlay"
        muted
        playsinline
        loop
      ></video>
      <transition name="opacity">
        <button
          class="ar-video__select"
          @click="setArArea"
          v-if="currentAction === 'area'"
        >
          <div class="ar-video__select-back"></div>
          <div class="ar-video__select-inner">
            <img src="~assets/image/call/ic-ar-field.svg" />
            <p>{{ $t('service.ar_area') }}</p>
            <p
              class="description"
              v-html="$t('service.ar_area_description')"
            ></p>
          </div>
        </button>
      </transition>
      <ar-pointing
        class="ar-pointing"
        :videoSize="videoSize"
        v-if="currentAction === 'pointing' && canPointing"
      ></ar-pointing>
    </div>
  </div>
</template>

<script>
import { mapGetters, mapActions } from 'vuex'
import { VIEW, ACTION } from 'configs/view.config'
import { AR_DRAWING } from 'configs/remote.config'
import toastMixin from 'mixins/toast'

import ArPointing from './ArPointing'
export default {
  name: 'ARVideo',
  mixins: [toastMixin],
  components: {
    ArPointing,
  },
  props: {
    canPointing: {
      type: Boolean,
      default: true,
    },
  },
  data() {
    return {
      chunk: [],
      videoSize: {
        width: 0,
        height: 0,
      },
      loaded: false,
    }
  },
  computed: {
    ...mapGetters(['mainView', 'view', 'viewAction', 'resolutions']),
    currentAction() {
      if (this.view !== VIEW.AR) return ''
      if (this.viewAction === ACTION.AR_AREA) {
        return 'area'
      } else if (this.viewAction === ACTION.AR_POINTING) {
        return 'pointing'
      } else if (this.viewAction === ACTION.AR_DRAWING) {
        return 'drawing'
      } else {
        return ''
      }
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
  watch: {
    mainView: {
      deep: true,
      handler(view) {
        if (!view.id) {
          this.loaded = false
          const videoBox = this.$el.querySelector('.main-video__box')
          if (videoBox) {
            videoBox.style.height = '100%'
            videoBox.style.width = '100%'
          }
        }
      },
    },
    view(val) {
      if (val === VIEW.AR) {
        this.$refs['arVideo'].play()
        setTimeout(() => {
          this.optimizeVideoSize()
        }, 500)
      } else {
        this.$refs['arVideo'].pause()
      }
    },
    resolution: {
      deep: true,
      handler() {
        this.optimizeVideoSize()
      },
    },
    currentAction(val) {
      if (this.view !== VIEW.AR) return
      if (val === 'pointing') {
        this.toastDefault(this.$t('service.ar_pointing_max'))
      } else if (val === 'area') {
        this.toastDefault(this.$t('service.ar_area_setting'))
      } else if (val === 'drawing') {
        this.toastDefault(this.$t('service.ar_area_success'))
      }
    },
  },
  methods: {
    ...mapActions(['showArImage']),
    setArArea() {
      this.$call.sendArDrawing(AR_DRAWING.FRAME_REQUEST, {}, [
        this.mainView.connectionId,
      ])
    },
    mediaPlay() {
      setTimeout(() => {
        this.optimizeVideoSize()
        this.loaded = false
      }, 3000)
    },
    optimizeVideoSize() {
      console.log('optimizeVideoSize ar video')
      if (this.view !== VIEW.AR) return
      const mainWrapper = this.$el
      const videoBox = this.$el.querySelector('.ar-video__box')
      const video = this.$refs['arVideo']
      const videoWidth = video.videoWidth
      const videoHeight = video.videoHeight
      if (this.resolution.width === 0 || this.resolution.height === 0) return
      this.debug(
        'current resolution: ',
        this.resolution.width,
        this.resolution.height,
      )
      this.debug('current video: ', videoWidth, videoHeight)

      let maxWidth = mainWrapper.offsetWidth
      let maxHeight = mainWrapper.offsetHeight
      let scale = videoWidth / videoHeight
      let width = 0
      let height = 0
      if (videoWidth / videoHeight < maxWidth / maxHeight) {
        // height에 맞춤
        height = maxHeight
        width = maxHeight * scale
        // video.style.height = maxHeight + 'px'
        // video.style.width = maxHeight * scale + 'px'
      } else {
        // width에 맞춤
        height = maxWidth / scale
        width = maxWidth
        // video.style.height = maxWidth / scale + 'px'
        // video.style.width = maxWidth + 'px'
      }
      videoBox.style.width = width + 'px'
      // videoBox.style.height = height + 'px'
      videoBox.style.height = '100%'
      this.videoSize.width = width
      this.videoSize.height = height
      this.debug('calc size: ', this.videoSize.width, this.videoSize.height)
    },
    windowResize() {
      setTimeout(() => {
        this.optimizeVideoSize()
      }, 3000)
    },
  },

  /* Lifecycles */
  created() {
    window.addEventListener('resize', this.windowResize)
  },
  mounted() {
    if (this.resolution && this.resolution.width > 0) {
      setTimeout(() => {
        this.optimizeVideoSize()
      }, 3000)
    }
  },
  beforeDestroy() {
    window.removeEventListener('resize', this.windowResize)
  },
}
</script>
