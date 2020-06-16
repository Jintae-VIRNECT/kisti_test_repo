<template>
  <div class="ar-video">
    <div class="ar-video__box">
      <video
        class="ar-video__stream"
        :srcObject.prop="mainView.stream"
        @play="optimizeVideoSize"
        :muted="!speaker"
        autoplay
        playsinline
        loop
      ></video>
      <transition name="opacity">
        <button
          class="ar-video__select"
          @click="setArArea"
          v-if="viewAction === AR_AREA"
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
        v-if="viewAction === AR_POINTING"
      ></ar-pointing>
    </div>
  </div>
</template>

<script>
import { mapGetters } from 'vuex'
import { ACTION } from 'configs/view.config'
import ArPointing from './ArPointing'
import { EVENT } from 'configs/drawing.config'
export default {
  name: 'ARVideo',
  components: {
    ArPointing,
  },
  data() {
    return {
      AR_AREA: ACTION.AR_AREA,
      AR_POINTING: ACTION.AR_POINTING,
    }
  },
  computed: {
    ...mapGetters(['mainView', 'speaker', 'viewAction', 'resolutions']),
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
    resolution: {
      deep: true,
      handler() {
        this.optimizeVideoSize()
      },
    },
  },
  methods: {
    setArArea() {
      this.$call.arDrawing(EVENT.REQUEST_FRAME)
    },
    optimizeVideoSize() {
      const mainWrapper = this.$el
      const videoBox = this.$el.querySelector('.ar-video__box')
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
  },

  /* Lifecycles */
  mounted() {
    if (this.resolution && this.resolution.width > 0) {
      this.optimizeVideoSize()
    }
  },
}
</script>
