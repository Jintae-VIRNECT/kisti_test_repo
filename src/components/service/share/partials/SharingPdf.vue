<template>
  <li class="sharing-image" @click="pdfPageView">
    <button class="sharing-image__item pdf">
      <img :src="thumbnail" />
    </button>
    <p class="sharing-image__name">{{ fileData.name }}</p>
    <button class="sharing-image__remove">파일 삭제</button>
    <div
      class="sharing-image__loading"
      v-if="loadCount === 0 || loadCount !== totalPages"
    >
      <div class="loading-box">
        <p class="loading-box__title">변환중</p>
        <div class="loading-box__progress">
          <div
            :style="{ width: `${(loadCount / totalPages) * 100}%` }"
            class="loading-box__progress-bar"
          ></div>
        </div>
        <p class="loading-box__dash">{{ `(${loadCount}/${totalPages})` }}</p>
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
      // docPages: [],
      loadCount: 0,
    }
  },
  props: {
    fileInfo: {
      type: Object,
    },
  },
  computed: {
    ...mapGetters({
      pdfPages: 'pdfPages',
    }),
    docPages() {
      return this.pdfPages[this.fileInfo.id]
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
    loadCount(val) {
      if (val === this.totalPages) {
        const fileReader = new FileReader()
        fileReader.onload = e => {
          this.thumbnail = e.target.result
        }
        fileReader.readAsDataURL(this.docPages[0].filedata)
      }
    },
  },
  methods: {
    ...mapActions(['addPdfPage']),
    init() {
      PDFJS.getDocument(URL.createObjectURL(this.fileData))
        .then(pdfDocument => {
          const updateData = Object.assign({}, this.fileInfo)
          updateData.pages = new Array(pdfDocument.numPages)

          this.document = pdfDocument
          // 페이지 별 로드처리
          for (let index = 1; index <= pdfDocument.numPages; index++) {
            this.getPage(index, pdfDocument)
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
    getPage(index) {
      this.document.getPage(index).then(fileReader => {
        const canvas = document.createElement('canvas')
        const viewport = fileReader.getViewport(1)
        const renderTask = fileReader.render({
          canvasContext: canvas.getContext('2d'),
          viewport,
        })
        canvas.width = viewport.width
        canvas.height = viewport.height
        renderTask.promise.then(() => {
          canvas.toBlob(
            blobData => {
              blobData.name = fileReader.pageNumber
              const docPage = {
                id: this.fileInfo.id,
                pageNum: fileReader.pageNumber,
                filedata: blobData,
              }

              this.loadCount += 1
              this.addPdfPage(docPage)
              // this.docPages.push(docPage)
            },
            'image/jpeg',
            1,
          )
        })
      })
    },
    pdfPageView() {
      this.$emit('pdfView', this.fileInfo.id)
    },
  },

  /* Lifecycles */
  mounted() {
    console.log(this.fileInfo)
    this.init()
  },
}
</script>
