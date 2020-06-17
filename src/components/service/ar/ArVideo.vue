<template>
  <div class="ar-video">
    <div class="ar-video__box">
      <video
        class="ar-video__stream"
        ref="arVideo"
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
          v-if="view === 'area'"
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
      <ar-pointing class="ar-pointing" v-if="view === 'pointing'"></ar-pointing>
    </div>
  </div>
</template>

<script>
import { mapGetters, mapActions } from 'vuex'
import { ACTION } from 'configs/view.config'
import ArPointing from './ArPointing'
import { SIGNAL, AR_DRAWING } from 'configs/remote.config'
import toastMixin from 'mixins/toast'
import web_test from 'utils/testing'
export default {
  name: 'ARVideo',
  mixins: [toastMixin],
  components: {
    ArPointing,
  },
  data() {
    return {
      chunk: [],
    }
  },
  computed: {
    ...mapGetters(['mainView', 'speaker', 'viewAction', 'resolutions']),
    view() {
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
    resolution: {
      deep: true,
      handler() {
        this.optimizeVideoSize()
      },
    },
    view(val) {
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
      this.$call.arDrawing(AR_DRAWING.REQUEST_FRAME)
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
    receiveCapture(receive) {
      const data = JSON.parse(receive.data)

      // 웹-웹 테스트용
      if (web_test) {
        if (data.type === AR_DRAWING.REQUEST_FRAME) {
          this.doArCapture()
          return
        }
      }
      if (data.from === this.account.uuid) return

      // frameResponse 수신
      if (data.type !== AR_DRAWING.FRAME_RESPONSE) return

      if (data.status === 'firstFrame') {
        this.chunk = []
      }
      this.chunk.push(data.chunk)

      if (data.status === 'lastFrame') {
        this.encodeImage(data.imgId)
      }
    },
    encodeImage(imgId) {
      let imgUrl = ''
      for (let part of this.chunk) {
        imgUrl += part
      }
      this.chunk = []
      imgUrl = 'data:image/png;base64,' + imgUrl
      const imageInfo = {
        id: imgId,
        img: imgUrl,
      }

      this.showArImage(imageInfo)
      this.$call.arDrawing(AR_DRAWING.RECEIVE_FRAME)
    },
    /**
     * 웹-웹 테스트용!
     */
    doArCapture() {
      const videoEl = this.$el.querySelector('.ar-video__stream')

      const width = videoEl.offsetWidth
      const height = videoEl.offsetHeight

      const tmpCanvas = document.createElement('canvas')
      tmpCanvas.width = width
      tmpCanvas.height = height

      const tmpCtx = tmpCanvas.getContext('2d')

      tmpCtx.drawImage(this.$refs['arVideo'], 0, 0, width, height)

      const imgUrl = tmpCanvas.toDataURL('image/png')

      this.sendFrame(imgUrl, Date.now())
    },
    sendFrame(imgUrl, id) {
      const params = {
        imgId: id,
      }
      const chunkSize = 1024 * 10

      const chunk = []
      const base64 = imgUrl.replace(/data:image\/.+;base64,/, '')
      const chunkLength = parseInt(base64.length / chunkSize)
      let start = 0
      for (let i = 0; i < chunkLength; i++) {
        chunk.push(base64.substr(start, chunkSize))
        start += chunkSize
      }
      for (let i = 0; i < chunk.length; i++) {
        if (i === 0) params.status = 'firstFrame'
        else if (i === chunk.length - 1) params.status = 'lastFrame'
        else params.status = 'frame'
        params.chunk = chunk[i]
        this.$call.arDrawing(AR_DRAWING.FRAME_RESPONSE, params)
      }
    },
  },

  /* Lifecycles */
  created() {
    this.$call.addListener(SIGNAL.AR_DRAWING, this.receiveCapture)
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
