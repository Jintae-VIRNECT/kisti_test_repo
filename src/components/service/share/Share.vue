<template>
  <div class="share">
    <ul class="share-title">
      <li>
        <button
          class="share-title__button"
          :class="{ active: ['file', 'pdfview'].indexOf(list) > -1 }"
          @click="changeTab('file')"
        >
          파일목록
        </button>
      </li>
      <li>
        <button
          class="share-title__button"
          :class="{ active: list === 'history' }"
          @click="changeTab('history')"
        >
          히스토리
        </button>
      </li>
    </ul>
    <file-list v-show="list === 'file'" @pdfView="changePdfView"></file-list>
    <history-list v-show="list === 'history'"></history-list>
    <pdf-view v-show="list === 'pdfview'" :id="fileId"></pdf-view>
  </div>
</template>

<script>
import FileList from './partials/ShareFileList'
import HistoryList from './partials/ShareHistoryList'
import PdfView from './partials/SharePdfView'
export default {
  name: 'Share',
  components: {
    FileList,
    HistoryList,
    PdfView,
  },
  data() {
    return {
      list: 'file',
      fileId: 0,
    }
  },
  methods: {
    changePdfView(id) {
      this.fileId = id
      this.changeTab('pdfview')
    },
    changeTab(val) {
      this.list = val
    },
  },

  /* Lifecycles */
  mounted() {},
}
</script>
