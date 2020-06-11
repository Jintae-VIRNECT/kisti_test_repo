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
    <pdf-view
      v-show="list === 'pdfview'"
      :file="file"
      @back="changeTab('file')"
    ></pdf-view>
  </div>
</template>

<script>
import { mapGetters } from 'vuex'
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
      file: {},
    }
  },
  computed: {
    ...mapGetters(['shareFile']),
  },
  watch: {
    shareFile: {
      deep: true,
      handler(e) {
        if (e && e.id) {
          this.changeTab('history')
        }
      },
    },
  },
  methods: {
    changePdfView(fileInfo) {
      this.changeTab('pdfview', fileInfo)
    },
    changeTab(val, fileInfo) {
      if (!fileInfo) fileInfo = {}
      this.file = fileInfo
      this.list = val
    },
  },

  /* Lifecycles */
  mounted() {},
}
</script>
