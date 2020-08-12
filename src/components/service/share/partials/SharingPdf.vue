<template>
  <li class="sharing-image" @click="pdfPageView">
    <button class="sharing-image__item pdf">
      <img :src="thumbnail" />
    </button>

    <p class="sharing-image__name">{{ fileData.name }}</p>
    <button class="sharing-image__remove" @click.stop="deleteImage">
      {{ $t('service.file_remove') }}
    </button>
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
  </li>
</template>

<script>
import { mapGetters, mapActions } from 'vuex'
import toastMixin from 'mixins/toast'
import confirmMixin from 'mixins/confirm'
import PDFJS from 'pdfjs-dist'
PDFJS.GlobalWorkerOptions.workerSrc = 'pdfjs-dist/build/pdf.worker.js'
export default {
  name: 'SharingPdf',
  mixins: [toastMixin, confirmMixin],
  components: {},
  data() {
    return {
      thumbnail: '',
      document: null,
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
            fileReader.readAsDataURL(this.docPages[0].filedata)
          })
        }
      },
    },
  },
  methods: {
    ...mapActions(['addPdfPage', 'removePdfPage', 'removeFile']),
    init() {
      if (
        this.docPages.length !== 0 &&
        this.docPages.length === this.totalPages
      )
        return
      this.removePdfPage(this.fileInfo.id)
      PDFJS.getDocument(URL.createObjectURL(this.fileData))
        .then(async pdfDocument => {
          const updateData = Object.assign({}, this.fileInfo)
          updateData.pages = new Array(pdfDocument.numPages)

          this.document = pdfDocument
          // 페이지 별 로드처리
          for (let index = 1; index <= pdfDocument.numPages; index++) {
            await this.getPage(index, pdfDocument)
          }
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
    async getPage(index) {
      const fileReader = await this.document.getPage(index)
      const canvas = document.createElement('canvas')
      const viewport = fileReader.getViewport(1)
      const renderTask = fileReader.render({
        canvasContext: canvas.getContext('2d'),
        viewport,
      })
      canvas.width = viewport.width
      canvas.height = viewport.height
      await renderTask.promise
      const blobData = await this.getCanvasBlob(canvas)
      blobData.name = fileReader.pageNumber
      const docPage = {
        id: this.fileInfo.id,
        pageNum: fileReader.pageNumber,
        filedata: blobData,
      }
      await this.addPdfPage(docPage)
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
  },

  /* Lifecycles */
  mounted() {
    this.init()
  },
}
</script>
