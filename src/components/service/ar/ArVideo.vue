<template>
  <div class="ar-video">
    <div class="ar-video__box">
      <video
        class="ar-video__stream"
        ref="arVideo"
        :srcObject.prop="mainView.stream"
        @play="optimizeVideoSize"
        :muted="!speaker"
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
            <p>AR 영역 설정</p>
            <p class="description">
              AR 영역을 설정합니다. 설정된 영역에서 <br />
              AR 기능을 실행합니다.
            </p>
          </div>
        </button>
      </transition>
      <ar-pointing
        class="ar-pointing"
        :videoSize="videoSize"
        v-if="currentAction === 'pointing'"
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
  data() {
    return {
      chunk: [],
      videoSize: {
        width: 0,
        height: 0,
      },
    }
  },
  computed: {
    ...mapGetters(['mainView', 'speaker', 'view', 'viewAction', 'resolutions']),
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
    view(val) {
      if (val === VIEW.AR) {
        this.$refs['arVideo'].play()
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
        this.toastDefault(
          'AR 3D 화살표를 원하는 위치에 클릭하세요. 최대 30개의 화살표를 생성할 수 있습니다.',
        )
      } else if (val === 'area') {
        this.toastDefault(
          'AR 영역을 설정합니다. 설정된 영역에서 AR 기능을 실행합니다.',
        )
      } else if (val === 'drawing') {
        this.toastDefault('AR 영역이 설정되었습니다. AR 드로잉을 시작하세요.')
      }
    },
  },
  methods: {
    ...mapActions(['showArImage', 'setAction']),
    setArArea() {
      this.$call.arDrawing(AR_DRAWING.FRAME_REQUEST)
    },
    optimizeVideoSize() {
      if (this.view !== VIEW.AR) return
      const mainWrapper = this.$el
      const videoBox = this.$el.querySelector('.ar-video__box')
      const video = this.$refs['arVideo']
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
    },
  },

  /* Lifecycles */
  created() {
    window.addEventListener('resize', this.optimizeVideoSize)
  },
  mounted() {
    if (this.resolution && this.resolution.width > 0) {
      this.optimizeVideoSize()
    }
  },
  beforeDestroy() {
    window.removeEventListener('resize', this.optimizeVideoSize)
  },
}
</script>
