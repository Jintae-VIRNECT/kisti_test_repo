<template>
  <li
    class="upload--nav__item pdf"
    :class="{ loading: !data.loaded }"
    @click="itemClickHandler()"
    @dblclick="itemDblclickHandler()"
  >
    <figure class="file">
      <div class="file--thumb">
        <img :src="data.thumbnail" />

        <div class="file--load-counter" v-if="loadCount < data.pages.length">
          <p>{{ $t('service.call_upload_convert') }}</p>
          <div class="load-bar">
            <span
              :style="{ width: `${(loadCount / data.pages.length) * 100}px` }"
            ></span>
          </div>
          <p class="txt-count">{{ `${loadCount} / ${data.pages.length}` }}</p>
        </div>

        <div class="pdf-icon">
          <img src="~assets/image/ic-pdf-file@2x.png" />
        </div>
      </div>
      <figcaption class="file--name">{{ data.filedata.name }}</figcaption>

      <button
        class="file--delete"
        @click.stop="$store.dispatch('removeLocalDoc', data)"
      >
        delete file
      </button>
    </figure>
  </li>
</template>
<script>
import { mapGetters } from 'vuex'
import PDFJS from 'pdfjs-dist'
// import worker from 'pdfjs-dist/build/pdf.worker'
PDFJS.GlobalWorkerOptions.workerSrc = 'pdfjs-dist/build/pdf.worker.js'

export default {
  name: 'PdfViewer',
  props: {
    data: Object,
    selected: Object,
  },
  data() {
    return {
      document: null,
      loadCount: 0,
    }
  },
  computed: {
    ...mapGetters(['shareDocListLen']),
    loadedCounter() {
      return this.data.pages.filter(_ => _ !== undefined).length
    },
  },
  methods: {
    loadPdfFile() {
      PDFJS.getDocument(URL.createObjectURL(this.data.filedata))
        .then(pdfDocument => {
          // console.log(pdfDocument)
          const updateData = Object.assign({}, this.data)
          updateData.pages = new Array(pdfDocument.numPages)

          this.document = pdfDocument
          this.$store.dispatch('updateLocalDoc', updateData).then(() => {
            // 페이지 별 로드처리
            for (let index = 1; index <= pdfDocument.numPages; index++) {
              this.getPage(index, pdfDocument)
            }
          })
        })
        .catch(err => {
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
              const docPage = {
                id: this.data.id,
                pageNum: fileReader.pageNumber,
                data: blobData,
              }

              this.loadCount += 1
              this.$store.dispatch('updateLocalDocPage', docPage)
            },
            'image/jpeg',
            1,
          )
        })
      })
    },
    itemClickHandler() {
      if (this.loadCount === this.data.pages.length) {
        this.$emit('update:selected', this.data)
      }
    },
    itemDblclickHandler() {
      // console.log('dblclick')
    },
  },

  /* Lifecycles */
  mounted() {
    if (this.loadedCounter === this.data.pages.length) {
      this.loadCount = this.loadedCounter
    } else {
      this.loadPdfFile()
    }
  },
}
</script>
