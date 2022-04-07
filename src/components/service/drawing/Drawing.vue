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
      @exitDrawing="exitDrawing"
      :showExitButton="isDrawingExitVisible"
    >
    </drawing-canvas>
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
        <img :src="emptyDefaultImgPath" />
        <p
          v-html="
            isMobileSize
              ? $t('service.file_maxsize')
              : $t('service.drawing_drag')
          "
        ></p>
        <p
          class="description"
          v-html="
            isMobileSize
              ? $t('service.file_upload_mobile_guide')
              : $t('service.file_maxsize')
          "
        ></p>
        <button v-if="!isMobileSize" class="btn" @click="addFile">
          {{ $t('service.drawing_import') }}
        </button>
      </div>
    </div>
  </div>
</template>

<script>
import DrawingCanvas from './DrawingCanvas'
import { mapGetters, mapActions } from 'vuex'
import { ROLE } from 'configs/remote.config'
import toastMixin from 'mixins/toast'
import confirmMixin from 'mixins/confirm'
import shareFileMixin from 'mixins/shareFile'
import drawingMixin from 'mixins/drawing/drawing'

export default {
  name: 'Drawing',
  mixins: [toastMixin, confirmMixin, shareFileMixin, drawingMixin],
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
    emptyDefaultImgPath() {
      return this.isMobileSize
        ? require('assets/image/call/img_file_new.svg')
        : require('assets/image/call/img_file.svg')
    },
    isLeader() {
      return this.account.roleType === ROLE.LEADER
    },
    //협업보드 종료 버튼 표시 여부
    isDrawingExitVisible() {
      //리더이고, 협업보드에 공유된 파일이 있을 경우만 표시
      if (this.isLeader && this.shareFile && this.shareFile.id) return true
      return false
    },
  },
  methods: {
    ...mapActions(['addHistory', 'setView', 'showImage']),
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
    exitDrawing() {
      if (!this.isLeader) return
      this.$_exitDrawing()
    },
  },
}
</script>
