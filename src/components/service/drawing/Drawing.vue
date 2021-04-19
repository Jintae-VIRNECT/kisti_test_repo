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
      ref="drawingLayout"
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
import confirmMixin from 'mixins/confirm'
import { drawingDownload } from 'api/http/drawing'

export default {
  name: 'Drawing',
  mixins: [confirmMixin],
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
    ...mapGetters(['fileList', 'shareFile', 'view', 'historyList', 'roomInfo']),
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
        if (this.account.roleType === ROLE.LEADER) {
          this.showImage({})
        }
      }
    },
  },
  methods: {
    ...mapActions(['showImage', 'addHistory', 'setView']),
    participantChange(connectionId) {
      if (this.account.roleType !== ROLE.LEADER) return
      if (this.shareFile && this.shareFile.id) {
        if (!this.shareFile.json || this.shareFile.json.length === 0) {
          this.sendImage(connectionId)
          return
        }
        this.confirmDefault(this.$t('service.drawing_sync'), {
          action: this.refreshCanvas,
        })
      }
    },
    sendImage(connectionId) {
      const params = {
        imgId: this.shareFile.id,
        imgName: this.shareFile.oriName
          ? this.shareFile.oriName
          : this.shareFile.fileName,
        image: this.shareFile.img,
      }
      this.$call.sendDrawing(
        DRAWING.FILE_SHARE,
        {
          name: this.shareFile.name,
          objectName: this.shareFile.objectName,
          contentType: this.shareFile.contextType,
          width: this.shareFile.width,
          height: this.shareFile.height,
          index: this.shareFile.index,
        },
        [connectionId],
      )
      this.$refs['drawingLayout'].sendImage(params, [connectionId])
    },
    refreshCanvas() {
      const imgId = parseInt(
        Date.now()
          .toString()
          .substr(-9),
      )
      this.addHistory({
        id: imgId,
        fileName: this.shareFile.fileName,
        oriName: this.shareFile.oriName,
        img: this.shareFile.img,
      })
    },
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
    async getImage(receive) {
      const data = JSON.parse(receive.data)

      if (data.type === DRAWING.END_DRAWING) {
        this.showImage({})
        this.setView(VIEW.STREAM)
        return
      }

      if (data.type === DRAWING.FILE_SHARE) {
        if (data.contentType === 'application/pdf') {
          this.$eventBus.$emit(`loadPdf_${data.objectName}`, data.index)
        } else {
          const res = await drawingDownload({
            sessionId: this.roomInfo.sessionId,
            workspaceId: this.workspace.uuid,
            objectName: data.objectName,
            userId: this.account.uuid,
          })
          this.addHistory({
            objectName: res.objectName,
            id: Date.now(),
            img: res.url,
            width: data.width,
            height: data.height,
            fileName: res.name,
          })
        }
      }
    },
    getHistoryObject() {
      // 모바일 수신부 타입: Int32
      const imgId = parseInt(
        Date.now()
          .toString()
          .substr(-9),
      )
      let fileName = this.fileData.name
      if (this.pdfName) {
        const idx = this.pdfName.lastIndexOf('.')
        fileName = `${this.pdfName.slice(0, idx)} [${this.pdfPage}].png`
      }
      return {
        id: imgId,
        fileName: fileName,
        oriName:
          this.pdfName && this.pdfName.length > 0
            ? this.pdfName
            : this.fileData.name,
        img: this.imageData,
        // fileData: this.fileData,
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
        fileName: data.imgName,
      }

      this.showImage(imageInfo)
    },
  },

  /* Lifecycles */
  created() {
    this.$eventBus.$on(SIGNAL.DRAWING, this.getImage)
    this.$eventBus.$on('participantChange', this.participantChange)
  },
  beforeDestroy() {
    this.$eventBus.$off(SIGNAL.DRAWING, this.getImage)
    this.$eventBus.$off('participantChange', this.participantChange)
  },
}
</script>
