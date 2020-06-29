<template>
  <li class="sharing-image" @click="pdfPageView">
    <button class="sharing-image__item pdf">
      <img :src="thumbnail" />
    </button>

    <p class="sharing-image__name">{{ fileData.name }}</p>
    <button class="sharing-image__remove" @click.stop="deleteImage">
      파일 삭제
    </button>
    <div
      class="sharing-image__loading"
      v-if="docPages.length === 0 || docPages.length !== totalPages"
    >
      <div class="loading-box">
        <p class="loading-box__title">변환중</p>
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
import PDFJS from 'pdfjs-dist'
PDFJS.GlobalWorkerOptions.workerSrc = 'pdfjs-dist/build/pdf.worker.js'
export default {
  name: 'SharingPdf',
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
    ...mapActions(['addPdfPage', 'removePdfPage']),
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
          console.error(err)
          if (err.name === 'InvalidPDFException') {
            alert('Invalid PDF File')
          } else {
            console.log(err)
          }
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
      await renderTask.promise.then(() => {
        canvas.toBlob(
          blobData => {
            blobData.name = fileReader.pageNumber
            const docPage = {
              id: this.fileInfo.id,
              pageNum: fileReader.pageNumber,
              filedata: blobData,
            }
            this.addPdfPage(docPage)
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
        '정말로 삭제하시겠습니까?',
        {
          text: '확인',
          action: this.remove,
        },
        {
          text: '취소',
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
