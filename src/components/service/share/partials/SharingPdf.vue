<template>
  <li class="sharing-image" @click="pdfPageView">
    <button class="sharing-image__item pdf">
      <img :src="thumbnail" />
    </button>

    <p class="sharing-image__name">{{ fileData.name }}</p>
    <div
      class="sharing-image__loading"
      v-if="docPages.length === 0 || docPages.length !== totalPages"
    >
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
    <canvas
      style=" z-index: -999;display: none; width: 100%; height: 100%;"
      ref="backCanvas"
    ></canvas>
    <button class="sharing-image__remove" @click.stop="deleteImage">
      {{ $t('service.file_remove') }}
    </button>
  </li>
</template>

<script>
import { mapGetters, mapActions } from 'vuex'
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
      thumbnail: '',
      document: null,
      size: {
        width: 300,
        height: 150,
      },
    }
  },
  props: {
    fileInfo: {
      type: Object,
    },
  },
  computed: {
    ...mapGetters(['pdfPages']),
    docPages() {
      if (this.fileInfo.id in this.pdfPages) {
        return this.pdfPages[this.fileInfo.id]
      } else {
        return []
      }
    },
    fileData() {
      if (this.fileInfo && this.fileInfo.filedata) {
        return this.fileInfo.filedata
      } else {
        return {}
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
  },
  watch: {
    docPages: {
      deep: true,
      handler(e) {
        if (e.length === this.totalPages) {
          const fileReader = new FileReader()
          fileReader.onload = e => {
            this.thumbnail = e.target.result
          }
          this.$nextTick(() => {
            if (this.docPages[0] && this.docPages[0].pageNum) {
              fileReader.readAsDataURL(this.docPages[0].filedata)
            }
          })
        }
      },
    },
  },
  methods: {
    ...mapActions(['addPdfPage', 'removePdfPage', 'removeFile', 'addHistory']),
    init() {
      if (
        this.docPages.length !== 0 &&
        this.docPages.length === this.totalPages
      )
        return
      this.removePdfPage(this.fileInfo.id)
      let startTime = Date.now()
      // PDFJS.GlobalWorkerOptions.workerSrc = '/pdf.worker'
      const loadingTask = PDFJS.getDocument(URL.createObjectURL(this.fileData))
      loadingTask.promise
        .then(async pdfDocument => {
          this.document = pdfDocument
          // 페이지 별 로드처리
          for (let index = 1; index <= pdfDocument.numPages; index++) {
            await this.getPage(index, pdfDocument.numPages)
          }
          let duration = Date.now() - startTime
          this.logger('pdf loading time', duration)
        })
        .catch(err => {
          if (err.name === 'InvalidPDFException') {
            this.toastError('Invalid PDF File.')
          } else if (err.name === 'PasswordException') {
            this.toastError(this.$t('service.share_locked'))
          } else {
            console.error(err)
          }
          setTimeout(() => {
            this.remove()
          }, 3000)
        })
    },
    async getPage(index, numPages) {
      const blobData = await this.loadPage(index)
      blobData.name = index
      const docPage = {
        id: this.fileInfo.id,
        total: numPages,
        pageNum: index,
        // pageData: fileReader,
        filedata: blobData,
      }
      this.addPdfPage(docPage)
    },
    async loadPage(index, scale = null) {
      const fileReader = await this.document.getPage(index)
      const canvas = document.createElement('canvas')
      if (scale === null) {
        const vp = fileReader.getViewport({ scale: 1 })
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
      return await this.getCanvasBlob(canvas)
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
      this.$emit('pdfView', this.fileInfo.id)
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
    remove() {
      this.removeFile(this.fileInfo.id)
    },
    async addPdfHistory(page) {
      const imageBlob = await this.loadPage(page, 1.5)

      const imgId = parseInt(
        Date.now()
          .toString()
          .substr(-9),
      )

      const idx = this.fileData.name.lastIndexOf('.')
      let fileName = `${this.fileData.name.slice(0, idx)} [${page}].png`

      const fileReader = new FileReader()
      fileReader.onload = async e => {
        let imgUrl = e.target.result
        const history = {
          id: imgId,
          fileName: fileName,
          oriName:
            this.pdfName && this.pdfName.length > 0
              ? this.pdfName
              : this.fileData.name,
          img: imgUrl,
        }
        this.addHistory(history)
      }
      fileReader.readAsDataURL(imageBlob)
    },
  },

  /* Lifecycles */
  mounted() {
    this.init()
    this.size.width = this.$el.querySelector('.sharing-image__item').offsetWidth
    this.size.height = this.$el.querySelector(
      '.sharing-image__item',
    ).offsetHeight
  },
  created() {
    this.$eventBus.$on(`loadPdf_${this.fileInfo.id}`, this.addPdfHistory)
  },
  beforeDestroy() {
    this.$eventBus.$off(`loadPdf_${this.fileInfo.id}`)
  },
}
</script>
