<template>
  <div class="ar-view">
    <ar-video></ar-video>
    <ar-canvas
      v-show="isDrawing"
      :file="shareArImage"
      @loading="loadingFrame = false"
    ></ar-canvas>
    <div class="ar-video__loading" v-if="loadingFrame">
      <div class="ar-video__loading-inner">
        <img src="~assets/image/gif_loading.svg" />
      </div>
    </div>
  </div>
</template>

<script>
import { mapGetters, mapActions } from 'vuex'
import { ACTION } from 'configs/view.config'
import { SIGNAL, AR_DRAWING } from 'configs/remote.config'
import web_test from 'utils/testing'

import ArVideo from './ArVideo'
import ArCanvas from './ardrawing/DrawingCanvas'
export default {
  name: 'ARView',
  components: {
    ArVideo,
    ArCanvas,
  },
  data() {
    return {
      chunk: [],
      loadingFrame: false,
    }
  },
  computed: {
    ...mapGetters(['view', 'viewAction', 'shareArImage']),
    isDrawing() {
      if (this.viewAction === ACTION.AR_DRAWING) {
        return true
      } else {
        return false
      }
    },
  },
  methods: {
    ...mapActions(['showArImage']),
    receiveCapture(receive) {
      const data = JSON.parse(receive.data)

      // 웹-웹 테스트용
      if (data.from === this.account.uuid) return
      // if (data.to !== this.account.uuid) return
      if (web_test) {
        if (data.type === AR_DRAWING.REQUEST_FRAME) {
          this.doArCapture()
          return
        }
      }

      // frameResponse 수신
      if (
        ![
          AR_DRAWING.FIRST_FRAME,
          AR_DRAWING.FRAME,
          AR_DRAWING.LAST_FRAME,
        ].includes(data.type)
      )
        return

      if (!this.loadingFrame) this.loadingFrame = true

      if (data.type === AR_DRAWING.FIRST_FRAME) {
        this.chunk = []
      }
      this.chunk.push(data.chunk)

      if (data.type === AR_DRAWING.LAST_FRAME) {
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
      console.log(this.$refs)

      tmpCtx.drawImage(
        document.querySelector('.ar-video__stream'),
        0,
        0,
        width,
        height,
      )

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
      const chunkLength = Math.ceil(base64.length / chunkSize)
      let start = 0
      for (let i = 0; i < chunkLength; i++) {
        chunk.push(base64.substr(start, chunkSize))
        start += chunkSize
      }
      for (let i = 0; i < chunk.length; i++) {
        params.chunk = chunk[i]
        if (i === 0) {
          this.$call.arDrawing(AR_DRAWING.FIRST_FRAME, params)
        } else if (i === chunk.length - 1) {
          this.$call.arDrawing(AR_DRAWING.LAST_FRAME, params)
        } else {
          this.$call.arDrawing(AR_DRAWING.FRAME, params)
        }
      }
    },
  },
  created() {
    this.$call.addListener(SIGNAL.AR_DRAWING, this.receiveCapture)
  },
}
</script>
