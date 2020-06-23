<template>
  <div
    class="drawing-box"
    @dragenter.stop.prevent="dragenterHandler"
    @dragleave.stop.prevent="dragleaveHandler"
    @dragover.stop.prevent="dragoverHandler"
    @drop.prevent="dropHandler"
  >
    <drawing-canvas
      v-if="show === 'file'"
      :file="shareFile"
      @initCanvas="loadingFrame = false"
    ></drawing-canvas>
    <div class="drawing-box__empty" v-else-if="show === 'upload'">
      <div class="drawing-box__empty-inner">
        <img src="~assets/image/call/img_fileshare.svg" />
        <p>
          파일을 더블클릭하면 <br />
          상대방에게 공유를 시작합니다
        </p>
      </div>
    </div>
    <div class="drawing-box__empty" v-else>
      <div class="drawing-box__empty-inner">
        <img src="~assets/image/call/img_file.svg" />
        <p>
          이미지 또는 PDF 파일을 여기 끌어다 놓거나 <br />
          '불러오기'버튼을 사용하세요.
        </p>
        <p class="description">
          20MB이하의 파일을 공유할 수 있습니다.
        </p>
        <button class="btn" @click="addFile">불러오기</button>
      </div>
    </div>
    <div class="drawing-box__empty loading" v-if="loadingFrame">
      <div class="drawing-box__empty-inner">
        <img src="~assets/image/gif_loading.svg" />
      </div>
    </div>
  </div>
</template>

<script>
import DrawingCanvas from './DrawingCanvas'
import { mapGetters, mapActions } from 'vuex'
import { SIGNAL, DRAWING } from 'configs/remote.config'
import { VIEW } from 'configs/view.config'

export default {
  name: 'Drawing',
  components: {
    DrawingCanvas,
  },
  data() {
    return {
      chunk: [],
      loadingFrame: false,
    }
  },
  computed: {
    ...mapGetters(['fileList', 'shareFile', 'view']),
    show() {
      if (this.shareFile && this.shareFile.id) {
        return 'file'
      } else if (this.fileList && this.fileList.length > 0) {
        return 'upload'
      } else {
        return 'default'
      }
    },
  },
  watch: {
    view(val) {
      if (val !== VIEW.DRAWING) {
        // clear image
        this.showImage({})
      }
    },
  },
  methods: {
    ...mapActions(['showImage']),
    addFile() {
      this.$eventBus.$emit('addFile')
    },
    dragenterHandler(event) {
      // console.log(event)
    },
    dragleaveHandler(event) {
      // console.log(event)
    },
    dragoverHandler(event) {
      // console.log(event)
    },
    dropHandler(event) {
      const file = event.dataTransfer.files[0]
      this.$eventBus.$emit('addFile', file)
    },
    getImage(receive) {
      const data = JSON.parse(receive.data)
      if (data.from === this.account.uuid) return

      if (
        ![DRAWING.FIRST_FRAME, DRAWING.FRAME, DRAWING.LAST_FRAME].includes(
          data.type,
        )
      )
        return

      if (data.type === DRAWING.FIRST_FRAME) {
        this.loadingFrame = true
        this.chunk = []
      }
      this.chunk.push(data.chunk)

      if (data.type === DRAWING.LAST_FRAME) {
        // this.loadingFrame = false
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

      this.showImage(imageInfo)
    },
  },

  /* Lifecycles */
  created() {
    this.$call.addListener(SIGNAL.DRAWING, this.getImage)
  },
}
</script>
