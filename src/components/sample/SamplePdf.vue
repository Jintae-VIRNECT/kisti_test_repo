<template>
  <section>
    <main class="test-content">
      <div clasds="pdf-choose">
        <input
          type="file"
          ref="inputUpload"
          accept="application/pdf"
          @change="loadFile($event)"
        />
      </div>
      <nav class="pdf-navigation">
        <ol ref="pdfNavigation" v-if="document">
          <li
            v-for="(page, index) in document.numPages"
            :key="index"
            @click="showSelectPage(index + 1)"
          >
            <canvas></canvas>
          </li>
        </ol>
      </nav>
      <div class="playground">
        <div class="playground-pager" v-if="document">
          <button @click="showPrevPage()">이전</button>
          <label for=""
            ><input type="text" v-model="currentPage" /> /
            {{ this.document && this.document.numPages }}</label
          >
          <button @click="showNextPage()">다음</button>
        </div>
        <canvas ref="PdfMainView"></canvas>
      </div>
    </main>
  </section>
</template>
<script>
import pdfjs from 'pdfjs-dist'
import worker from 'pdfjs-dist/build/pdf.worker'

export default {
  data() {
    return {
      currentPage: 1,
      document: null,
      pages: [],
    }
  },
  watch: {
    document(value) {
      if (value !== null) {
        this.pages = []
        this.currentPage = 1
      }
    },
    currentPage() {
      const self = this
      let current = parseInt(this.currentPage)

      current =
        current < this.document.numPages ? current : this.document.numPages
      current = current > 0 ? current : 0

      this.currentPage = current

      this.showSelectPage(current)
    },
  },
  methods: {
    loadFile(event) {
      const file = event.target.files[0]
      if (file) {
        this._loadFile(file)
      }
    },
    showNextPage() {
      // console.log(this.document)
      if (this.currentPage < this.document.numPages) {
        ++this.currentPage
      }
    },
    showPrevPage() {
      if (this.currentPage > 1) {
        --this.currentPage
      }
    },
    showSelectPage(index) {
      const page = this.pages[index - 1]
      const canvas = this.$refs['PdfMainView']
      const scaledViewport = page.getViewport(1)

      page.render({
        canvasContext: canvas.getContext('2d'),
        viewport: scaledViewport,
      })
      canvas.width = scaledViewport.width
      canvas.height = scaledViewport.height
    },
    _setNavigation(pdfDocument) {
      for (let index = 1; index <= pdfDocument.numPages; index++) {
        const canvas = this.$refs['pdfNavigation'].children[index - 1]
          .children[0]

        pdfDocument.getPage(index).then(page => {
          const scaledViewport = page.getViewport(
            180 / page.getViewport(1).height,
          )

          this.pages[index - 1] = page

          if (index === 1) {
            this.showSelectPage(index)
          }

          page.render({
            canvasContext: canvas.getContext('2d'),
            viewport: scaledViewport,
          })
          canvas.width = scaledViewport.width
          canvas.height = scaledViewport.height
        })
      }
    },
    _loadFile(file) {
      const self = this
      const reader = new FileReader()

      this.currentPage = 1
      this.document = null
      reader.addEventListener(
        'load',
        () => {
          pdfjs
            .getDocument(reader.result)
            .then(pdfDocument => {
              // console.log(pdfDocument);
              this.document = pdfDocument

              this.$nextTick(() => {
                this._setNavigation(pdfDocument)
              })
            })
            .catch(err => {
              if (err.name === 'InvalidPDFException') {
                alert('Invalid PDF File')
                this.document = null
                this.pages = null
                this.$refs['inputUpload'].value = ''
              }
            })
        },
        false,
      )
      reader.readAsDataURL(file)
    },

    setCanvas() {},
  },

  /* Lifecycles */
  mounted() {},
}
</script>
<style lang="scss">
.pdf-choose {
  clear: both;
}

.pdf-navigation {
  float: left;
  height: 720px;
  overflow: hidden;
  overflow-y: auto;
}

.playground {
  position: relative;
  margin-left: 150px;
  padding-top: 30px;

  &-pager {
    position: absolute;
    top: 0;
    right: 0;
  }
}
</style>
