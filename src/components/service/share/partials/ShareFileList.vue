<template>
  <div class="share-body__file">
    <vue2-scrollbar ref="upload-list-scroll">
      <ol class="upload-list" ref="upload-list">
        <li>
          <button class="upload-list__button" @click="addFileClick()">
            {{ $t('service.file_add') }}
          </button>
          <input
            type="file"
            ref="uploadFile"
            style="display:none;"
            accept="image/gif,image/bmp,image/jpeg,image/png,application/pdf"
            @change="fileChangeHandler"
          />
        </li>
        <template v-for="file of sharingList">
          <sharing-pdf
            v-if="file.contentType === 'application/pdf'"
            :ref="'sharing_' + file.objectName"
            :key="'sharing_' + file.objectName"
            :fileInfo="file"
            @pdfView="pdfView(file)"
          ></sharing-pdf>
          <sharing-image
            v-else
            :key="'sharing_' + file.objectName"
            :fileInfo="file"
          ></sharing-image>
        </template>
      </ol>
    </vue2-scrollbar>
  </div>
</template>

<script>
import { mapActions } from 'vuex'
import SharingImage from './SharingImage'
import SharingPdf from './SharingPdf'

import shareFileUploadMixin from 'mixins/shareFileUpload'
import fileShareEventQueueMixin from 'mixins/fileShareEventQueue'

import { drawingList, drawingDownload } from 'api/http/drawing'

import { SIGNAL, DRAWING } from 'configs/remote.config'

import { isOverflowY } from 'utils/element.js'

export default {
  name: 'ShareFileList',
  mixins: [shareFileUploadMixin, fileShareEventQueueMixin],
  components: {
    SharingImage,
    SharingPdf,
  },
  data() {
    return {
      sharingList: [],
      cbGetFileList: () => {},
    }
  },
  watch: {
    sharingList(val, oldVal) {
      if (val.length !== oldVal.length) {
        this.$nextTick(() => {
          const isNotOverflow = !isOverflowY(
            this.$refs['upload-list'],
            this.$refs['upload-list-scroll'].$el,
          )
          //overflow 해제 시 scroll을 최상위로 원위치
          if (isNotOverflow) this.$refs['upload-list-scroll'].scrollToY(0)
        })
      }
    },
  },
  methods: {
    ...mapActions(['addFile', 'addHistory']),
    pdfView(file) {
      this.$emit('pdfView', {
        id: file.objectName,
        name: file.name,
        objectName: file.objectName,
        contentType: file.contentType,
      })
    },
    addFileClick(file) {
      if (file) {
        this.loadFile(file, () => this.getFileList())
      } else {
        this.$refs['uploadFile'].click()
      }
    },
    fileChangeHandler(event) {
      const file = event.target.files[0]
      this.loadFile(file, () => this.getFileList())
    },
    async getFileList() {
      const res = await drawingList({
        sessionId: this.roomInfo.sessionId,
        workspaceId: this.workspace.uuid,
      })
      this.sharingList = res.fileInfoList
      this.$nextTick(() => {
        this.cbGetFileList()
      })
    },
    loadPdf(data) {
      if (this.sharingList.length === 0) {
        this.cbGetFileList = () => {
          this.cbGetFileList = () => {}
          this.$refs['sharing_' + data.objectName][0].addPdfHistory(
            data.index + 1,
          )
        }
        return
      }
      this.$refs['sharing_' + data.objectName][0].addPdfHistory(data.index + 1)
    },

    async fileShare({ data }) {
      if (data.type === DRAWING.FILE_SHARE) {
        if (data.contentType === 'application/pdf') {
          this.loadPdf(data)
          // this.$eventBus.$emit(`loadPdf_${data.objectName}`, data.index + 1)
        } else {
          const res = await drawingDownload({
            sessionId: this.roomInfo.sessionId,
            workspaceId: this.workspace.uuid,
            objectName: data.objectName,
            userId: this.account.uuid,
          })
          //image shareFile(vuex) set되는 곳
          this.addHistory({
            id: Date.now(),
            name: res.name,
            fileName: res.name,
            width: data.width,
            height: data.height,
            img: res.url,
            pageNum: 0, //image인 경우 pageNum은 0으로 보내도록한다.
            objectName: res.objectName,
            contentType: res.contentType,
          })
        }
      }
    },
  },

  /* Lifecycles */
  created() {
    this.$eventBus.$on('addFile', this.addFileClick)
    this.$eventBus.$on(SIGNAL.DRAWING_FROM_VUEX, this.fileShare) //vuex 큐에 임시 저장해두었다가 발생시키는 이벤트 리스너
    this.$eventBus.$on(SIGNAL.DRAWING, this.fileShare) //누락 이벤트가 모두 처리된 후 정상적으로 이벤트 직접 수신
    this.getFileList()
  },
  beforeDestroy() {
    this.$eventBus.$off('addFile', this.addFileClick)
    this.$eventBus.$off(SIGNAL.DRAWING_FROM_VUEX, this.fileShare)
    this.$eventBus.$off(SIGNAL.DRAWING, this.fileShare)
  },
}
</script>
