<template>
  <div
    class="drawing-box"
    @dragenter.stop.prevent="dragenterHandler"
    @dragleave.stop.prevent="dragleaveHandler"
    @dragover.stop.prevent="dragoverHandler"
    @drop.prevent="dropHandler"
  >
    <drawing-canvas
      v-show="show === 'file'"
      :file="shareFile"
      @loadingSuccess="loadingFrame = false"
      @loadingStart="loadingFrame = true"
    ></drawing-canvas>
    <transition name="loading">
      <div class="drawing-box__empty loading" v-if="loadingFrame">
        <div class="drawing-box__empty-inner">
          <img src="~assets/image/gif_loading.svg" />
        </div>
      </div>
    </transition>
    <div class="drawing-box__empty" v-if="show === 'upload'">
      <div class="drawing-box__empty-inner">
        <img src="~assets/image/call/img_fileshare.svg" />
        <p v-html="$t('service.drawing_dblclick')"></p>
      </div>
    </div>
    <div class="drawing-box__empty" v-show="show === 'default'">
      <div class="drawing-box__empty-inner">
        <img src="~assets/image/call/img_file.svg" />
        <p v-html="$t('service.drawing_drag')"></p>
        <p class="description">
          {{ $t('service.file_maxsize') }}
        </p>
        <button class="btn" @click="addFile">
          {{ $t('service.drawing_import') }}
        </button>
      </div>
    </div>
  </div>
</template>

<script>
import DrawingCanvas from './DrawingCanvas'
import { mapGetters, mapActions } from 'vuex'
import { SIGNAL, ROLE, DRAWING } from 'configs/remote.config'
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
        // TODO: 협업보드 나갈 때 클리어 할지 선택해야함
        if (this.account.roleType === ROLE.EXPERT_LEADER) {
          this.showImage({})
        }
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
        this.encodeImage(data)
      }
    },
    encodeImage(data) {
      let imgUrl = ''
      for (let part of this.chunk) {
        imgUrl += part
      }
      this.chunk = []
      imgUrl = 'data:image/png;base64,' + imgUrl
      const imageInfo = {
        id: data.imgId,
        img: imgUrl,
        width: data.width,
        height: data.height,
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
