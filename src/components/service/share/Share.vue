<template>
  <div class="share">
    <ul class="share-title">
      <li>
        <button
          class="share-title__button"
          :class="{
            active: ['file', 'pdfview'].indexOf(list) > -1,
            disable: !isLeader,
          }"
          @click="changeTab('file')"
        >
          {{ $t('service.file_list') }}
        </button>
      </li>
      <li v-if="isLeader">
        <button
          class="share-title__button"
          :class="{ active: list === 'history' }"
          @click="changeTab('history')"
        >
          {{ $t('service.share_list') }}
        </button>
      </li>
    </ul>
    <transition name="share-list__left">
      <div class="share-body" v-show="list === 'file'">
        <transition name="share-list__left">
          <file-list
            ref="shareFileList"
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
    <transition v-if="isLeader" name="share-list__right">
      <history-list
        ref="shareHistoryList"
        v-show="list === 'history'"
      ></history-list>
    </transition>
  </div>
</template>

<script>
import { mapGetters } from 'vuex'
import { SIGNAL, DRAWING, ROLE } from 'configs/remote.config'
import FileList from './partials/ShareFileList'
import PdfView from './partials/SharePdfView'
import HistoryList from './partials/ShareHistoryList'
import { DEVICE } from 'configs/device.config'
import toastMixin from 'mixins/toast'
export default {
  name: 'Share',
  mixins: [toastMixin],
  components: {
    FileList,
    HistoryList,
    // HistoryList: import('./partials/ShareHistoryList'),
    PdfView,
  },
  data() {
    return {
      list: 'file',
      file: {},
    }
  },
  computed: {
    ...mapGetters(['shareFile', 'participants']),
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
    isLeader() {
      return this.account.roleType === ROLE.LEADER
    },
  },
  watch: {
    shareFile: {
      deep: true,
      handler(e) {
        if (e && e.objectName && this.isLeader) {
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
      // this.$eventBus.$emit('scroll:reset')
    },
    signalDrawing({ data, receive }) {
      if (data.type === DRAWING.ADDED) {
        this.$refs['shareFileList'].getFileList()

        if (this.isLeader) {
          const fromConnectionId = receive.from.connectionId
          const fromUser = this.participants.find(pt => {
            return pt.connectionId === fromConnectionId
          })

          if (fromUser && fromUser.deviceType === DEVICE.GLASSES) {
            this.toastDefault(
              this.$t('service.toast_captured_image_uploaded', {
                name: fromUser.nickname,
              }),
            )
          }
        }

        this.changeTab('file')
      } else if (data.type === DRAWING.DELETED) {
        this.$refs['shareFileList'].getFileList()
      }
    },
  },

  /* Lifecycles */
  created() {
    this.$eventBus.$on(SIGNAL.DRAWING, this.signalDrawing)
  },
  beforeDestroy() {
    this.$eventBus.$off(SIGNAL.DRAWING, this.signalDrawing)
  },
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
