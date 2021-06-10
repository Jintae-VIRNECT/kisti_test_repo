<template>
  <li class="sharing-image" @click="pdfPageView">
    <button
      class="sharing-image__item pdf"
      :class="{
        active: shareFile.objectName === fileInfo.objectName,
      }"
    >
      <img :src="fileInfo.thumbnailDownloadUrl" />
      <div class="sharing-image__item-active">
        <p>{{ $t('service.share_current') }}</p>
      </div>
    </button>

    <p class="sharing-image__name">{{ fileInfo.name }}</p>
    <div class="sharing-image__loading" v-if="!loaded">
      <div class="loading-box">
        <p class="loading-box__title">
          {{ $t('service.share_loading') }}
        </p>
        <div class="loading-box__progress">
          <div
            :style="{ width: `${(docPages.length / totalPages) * 100}%` }"
            class="loading-box__progress-bar"
          ></div>
        </div>
        <p class="loading-box__dash">
          {{ `(${docPages.length}/${totalPages})` }}
        </p>
      </div>
    </div>
    <button class="sharing-image__remove" @click.stop="deleteImage">
      {{ $t('service.file_remove') }}
    </button>
  </li>
</template>

<script>
import { mapGetters, mapActions } from 'vuex'
import { DRAWING } from 'configs/remote.config'
import { drawingRemove, drawingDownload } from 'api/http/drawing'
import toastMixin from 'mixins/toast'
import confirmMixin from 'mixins/confirm'
import PDFJS from 'pdfjs-dist'
// PDFJS.GlobalWorkerOptions.workerSrc = 'pdfjs-dist/build/pdf.worker.js'
// PDFJS.GlobalWorkerOptions.workerSrc =
//   'https://cdn.jsdelivr.net/npm/pdfjs-dist@2.4.456/build/pdf.worker.js'
PDFJS.GlobalWorkerOptions.workerSrc = '/pdf.worker'
export default {
  name: 'SharingPdf',
  mixins: [toastMixin, confirmMixin],
  components: {},
  data() {
    return {
      document: null,
      size: {
        width: 300,
        height: 150,
      },
      cbLoad: () => {},
    }
  },
  props: {
    fileInfo: {
      type: Object,
    },
  },
  computed: {
    ...mapGetters(['pdfPages', 'roomInfo', 'shareFile']),
    docPages() {
      if (this.fileInfo.objectName in this.pdfPages) {
        return this.pdfPages[this.fileInfo.objectName]
      } else {
        return []
      }
    },
    totalPages() {
      if (
        this.document &&
        this.document.numPages &&
        this.document.numPages > 0
      ) {
        return this.document.numPages
      } else {
        return 0
      }
    },
    loaded() {
      return (
        this.docPages.length !== 0 && this.docPages.length === this.totalPages
      )
    },
  },
  methods: {
    ...mapActions(['addPdfPage', 'removePdfPage', 'removeFile', 'addHistory']),
    async init() {
      if (
        this.docPages.length !== 0 &&
        this.docPages.length === this.totalPages
      )
        return
      this.removePdfPage(this.fileInfo.objectName)
      let startTime = Date.now()
      const url = await this.downloadPdfFile()
      // PDFJS.GlobalWorkerOptions.workerSrc = '/pdf.worker'
      // const loadingTask = PDFJS.getDocument(URL.createObjectURL(this.fileData))
      const loadingTask = PDFJS.getDocument(url)
      loadingTask.promise
        .then(async pdfDocument => {
          this.document = pdfDocument
          // 페이지 별 로드처리
          for (let index = 1; index <= pdfDocument.numPages; index++) {
            await this.getPage(index, pdfDocument.numPages)
          }
          let duration = Date.now() - startTime
          this.logger('pdf loading time', duration)
          this.cbLoad()
        })
        .catch(err => {
          if (
            err.name === 'InvalidPDFException' ||
            err.message === 'Invalid PDF structure.'
          ) {
            // this.toastError('Invalid PDF File.')
            this.toastError(this.$t('service.file_type'))
          } else if (
            err.name === 'PasswordException' ||
            err.message === 'No password given'
          ) {
            this.toastError(this.$t('service.share_locked'))
          } else {
            this.toastError(this.$t('service.file_type'))
          }
          // setTimeout(() => {
          //   this.remove()
          // }, 3000)
        })
    },
    async downloadPdfFile() {
      const res = await drawingDownload({
        sessionId: this.roomInfo.sessionId,
        workspaceId: this.workspace.uuid,
        objectName: this.fileInfo.objectName,
        userId: this.account.uuid,
      })
      return res.url
    },
    async getPage(index, numPages) {
      const { blob, origin } = await this.loadPage(index)
      blob.name = index
      const docPage = {
        id: this.fileInfo.objectName,
        total: numPages,
        pageNum: index,
        filedata: blob,
        width: origin.width,
        height: origin.height,
      }
      this.addPdfPage(docPage)
    },
    async loadPage(index, scale = null) {
      const fileReader = await this.document.getPage(index)
      const canvas = document.createElement('canvas')
      let vp = null
      if (scale === null) {
        vp = fileReader.getViewport({ scale: 1 })
        let scaleWidth = this.size.width / vp.width
        let scaleHeight = this.size.height / vp.height
        scale = scaleWidth > scaleHeight ? scaleHeight : scaleWidth
        if (scale > 0.1) scale = 0.1
      }
      const viewport = fileReader.getViewport({ scale: scale })
      const renderTask = fileReader.render({
        canvasContext: canvas.getContext('2d'),
        viewport,
      })
      canvas.width = viewport.width
      canvas.height = viewport.height
      await renderTask.promise
      const blob = await this.getCanvasBlob(canvas)
      return {
        blob,
        size: {
          width: canvas.width,
          height: canvas.height,
        },
        origin: {
          width: vp ? vp.width : 0,
          height: vp ? vp.height : 0,
        },
      }
    },
    getCanvasBlob(canvas) {
      return new Promise(resolve => {
        canvas.toBlob(
          blob => {
            resolve(blob)
          },
          'image/jpeg',
          1,
        )
      })
    },
    pdfPageView() {
      if (
        this.docPages.length === 0 ||
        this.docPages.length !== this.totalPages
      )
        return
      this.$emit('pdfView', this.fileInfo.objectName)
    },
    deleteImage() {
      this.confirmCancel(
        this.$t('service.share_delete_real'),
        {
          text: this.$t('button.confirm'),
          action: this.remove,
        },
        {
          text: this.$t('button.cancel'),
        },
      )
    },
    async remove() {
      try {
        await drawingRemove({
          workspaceId: this.workspace.uuid,
          sessionId: this.roomInfo.sessionId,
          leaderUserId: this.account.uuid,
          objectName: this.fileInfo.objectName,
        })

        this.$call.sendDrawing(DRAWING.DELETED, {
          objectNames: [this.fileInfo.objectName],
        })
        // this.removeFile(this.fileInfo.id)
      } catch (err) {
        console.log(err)
      }
    },
    async addPdfHistory(page) {
      if (!this.loaded) {
        this.cbLoad = () => {
          this.cbLoad = () => {}
          this.addPdfHistory(page)
        }
        return
      }
      const { blob, size } = await this.loadPage(page, 1.5)

      const imgId = parseInt(
        Date.now()
          .toString()
          .substr(-9),
      )

      const idx = this.fileInfo.name.lastIndexOf('.')
      let fileName = `${this.fileInfo.name.slice(0, idx)} [${page}].png`

      const fileReader = new FileReader()
      fileReader.onload = async e => {
        let imgUrl = e.target.result
        const history = {
          id: imgId,
          name: fileName,
          fileName: fileName,
          oriName:
            this.pdfName && this.pdfName.length > 0
              ? this.pdfName
              : this.fileInfo.name,
          width: size.width,
          height: size.height,
          img: imgUrl,
          pageNum: page,
          objectName: this.fileInfo.objectName,
          contentType: this.fileInfo.contentType,
        }
        //pdf shareFile(vuex) set 되는 곳
        this.addHistory(history)
      }
      fileReader.readAsDataURL(blob)
    },
  },

  /* Lifecycles */
  mounted() {
    this.init()
  },
}
</script>
