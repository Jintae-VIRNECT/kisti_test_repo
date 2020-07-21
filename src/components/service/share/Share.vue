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
          공유목록
        </button>
      </li>
    </ul>
    <transition name="share-list__left">
      <div class="share-body" v-show="list === 'file'">
        <transition name="share-list__left">
          <file-list
            v-show="!file || !file.id"
            @pdfView="changePdfView"
          ></file-list>
        </transition>
        <transition name="share-list__right">
          <pdf-view
            v-show="file && file.id"
            :file="file"
            @back="changeTab('file', 'empty')"
          ></pdf-view>
        </transition>
      </div>
    </transition>
    <transition name="share-list__right">
      <history-list v-show="list === 'history'"></history-list>
    </transition>
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
    show() {
      if (this.list === 'file') {
        if (!this.file || !this.file.id) {
          return 'file'
        } else {
          return 'pdf'
        }
      } else {
        return 'history'
      }
    },
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
      this.file = fileInfo
    },
    changeTab(val, fileInfo) {
      if (fileInfo === 'empty') {
        this.file = {}
      }
      this.list = val
      this.$eventBus.$emit('scroll:reset')
    },
  },

  /* Lifecycles */
  mounted() {},
}
</script>
<style>
.share-list__left-enter-active,
.share-list__left-leave-active,
.share-list__right-enter-active,
.share-list__right-leave-active {
  transition: left ease 0.4s;
}
.share-list__left-enter {
  left: -100%;
}
.share-list__left-enter-to {
  left: 0;
}
.share-list__left-leave {
  left: 0;
}
.share-list__left-leave-to {
  left: -100%;
}
.share-list__right-enter {
  left: 100%;
}
.share-list__right-enter-to {
  left: 0;
}
.share-list__right-leave {
  left: 0;
}
.share-list__right-leave-to {
  left: 100%;
}
</style>
