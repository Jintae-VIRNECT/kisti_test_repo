<template>
  <div class="share-body__file">
    <vue2-scrollbar>
      <ol class="upload-list">
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
import { mapActions, mapGetters } from 'vuex'
import SharingImage from './SharingImage'
import SharingPdf from './SharingPdf'
import toastMixin from 'mixins/toast'
import { drawingUpload, drawingList, drawingDownload } from 'api/http/drawing'
import { SIGNAL, DRAWING } from 'configs/remote.config'
const maxFileSize = 1024 * 1024 * 20
export default {
  name: 'ShareFileList',
  mixins: [toastMixin],
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
  computed: {
    ...mapGetters({
      roomInfo: 'roomInfo',
    }),
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
        this.loadFile(file)
      } else {
        this.$refs['uploadFile'].click()
      }
    },
    fileChangeHandler(event) {
      const file = event.target.files[0]
      this.loadFile(file)
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
    async loadFile(file) {
      if (file) {
        if (file.size > maxFileSize) {
          this.toastError(this.$t('service.file_maxsize'))
          this.clearUploadFile()
          return false
        }

        if (
          [
            'image/jpeg',
            'image/png',
            'image/bmp',
            'image/gif',
            'application/pdf',
          ].includes(file.type)
        ) {
          const res = await drawingUpload({
            file: file,
            sessionId: this.roomInfo.sessionId,
            userId: this.account.uuid,
            workspaceId: this.workspace.uuid,
          })
          this.$call.sendDrawing(DRAWING.ADDED, {
            deleted: false, //false
            expired: false, //false
            sessionId: res.sesssionId,
            name: res.name,
            objectName: res.objectName,
            contentType: res.contentType, // "image/jpeg", "image/bmp", "image/gif", "application/pdf",
            size: res.size,
            createdDate: res.createdDate,
            expirationDate: res.expirationDate,
            width: res.width, //pdf 는 0
            height: res.height,
          })
          this.clearUploadFile()
          this.getFileList()
        } else {
          this.toastError(this.$t('service.file_type'))
          return false
        }
      }
    },
    clearUploadFile() {
      this.$refs['uploadFile'].value = ''
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

    async fileShare(receive) {
      const data = JSON.parse(receive.data)

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
    this.$eventBus.$on(SIGNAL.DRAWING, this.fileShare)
    this.getFileList()
  },
  beforeDestroy() {
    this.$eventBus.$off('addFile', this.addFileClick)
    this.$eventBus.$off(SIGNAL.DRAWING, this.fileShare)
  },
}
</script>
