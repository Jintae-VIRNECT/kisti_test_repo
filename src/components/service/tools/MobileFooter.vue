<template>
  <footer class="mobile-service-footer">
    <div class="footer-tab-container">
      <button
        v-for="menu in tabMenus"
        :class="{
          active: view === menu.key,
          notice: menu.notice,
          invisible: menu.key === VIEW.AR && !isLeader,
        }"
        :key="menu.key"
        @click="goTab(menu.key)"
      >
        <span class="layer"></span>
        {{ $t(menu.text) }}
      </button>
    </div>
    <div class="footer-button-container">
      <template v-if="view === VIEW.STREAM">
        <mobile-more-button
          @selectMember="openParticipantModal"
        ></mobile-more-button>
        <mobile-capture-button
          :disabled="!isMainViewOn"
        ></mobile-capture-button>
        <mobile-flash-button></mobile-flash-button>
      </template>
      <template v-else-if="view === VIEW.DRAWING">
        <!-- 협업보드 종료 버튼 리더에게만 표시 -->
        <mobile-drawing-exit-button
          v-if="historyList.length > 0 && isLeader"
          @exitDrawing="onExitDrawing"
        ></mobile-drawing-exit-button>
        <mobile-file-list-button
          :disabled="fileList.length === 0"
          @openFileListModal="openFileListModal"
        ></mobile-file-list-button>
        <mobile-download-button
          :disabled="historyList.length === 0"
        ></mobile-download-button>
        <mobile-upload-button @uploaded="onFileUploaded"></mobile-upload-button>
      </template>
    </div>
    <div class="footer-modal-container">
      <mobile-participant-modal
        :visible.sync="isParticipantModalShow"
        :beforeClose="beforeClose"
      ></mobile-participant-modal>
      <mobile-share-file-list-modal
        ref="file-list"
        :modalShow.sync="isFileListModalShow"
        :fileList="fileList"
      ></mobile-share-file-list-modal>
    </div>
  </footer>
</template>

<script>
import tabChangeMixin from 'mixins/tabChange'
import MobileMoreButton from './partials/MobileMoreButton'
import MobileCaptureButton from './partials/MobileCaptureButton'
import MobileFlashButton from './partials/MobileFlashButton'
import MobileUploadButton from './partials/MobileUploadButton'
import MobileFileListButton from './partials/MobileFileListButton'
import MobileDownloadButton from './partials/MobileDownloadButton'
import MobileDrawingExitButton from './partials/MobileDrawingExitButton'

import fileShareEventQueueMixin from 'mixins/fileShareEventQueue'

import { drawingList, drawingDownload } from 'api/http/drawing'
import { mapGetters, mapActions } from 'vuex'
import { VIEW } from 'configs/view.config'
import { SIGNAL, DRAWING, ROLE } from 'configs/remote.config'

export default {
  mixins: [tabChangeMixin, fileShareEventQueueMixin],
  components: {
    MobileMoreButton,
    MobileCaptureButton,
    MobileFlashButton,
    MobileParticipantModal: () => import('../modal/MobileParticipantModal'),
    MobileShareFileListModal: () => import('../modal/MobileShareFileListModal'),
    MobileUploadButton,
    MobileFileListButton,
    MobileDownloadButton,
    MobileDrawingExitButton,
  },
  data() {
    return {
      VIEW: Object.freeze(VIEW),
      isParticipantModalShow: false,
      isFileListModalShow: false,
      fileList: [],
    }
  },
  computed: {
    ...mapGetters(['mainView', 'historyList', 'roomInfo']),
    isMainViewOn() {
      return this.mainView && this.mainView.id && this.mainView.video
    },
    isLeader() {
      return this.account.roleType === ROLE.LEADER
    },
  },
  methods: {
    ...mapActions(['addHistory']),
    openParticipantModal() {
      this.isParticipantModalShow = true
    },
    openFileListModal() {
      this.isFileListModalShow = true
    },
    onFileUploaded() {
      this.getFileList()
    },
    beforeClose() {
      this.isParticipantModalShow = false
    },
    async getFileList() {
      const res = await drawingList({
        sessionId: this.roomInfo.sessionId,
        workspaceId: this.workspace.uuid,
      })
      this.fileList = res.fileInfoList
    },
    signalDrawing({ data }) {
      if (data.type === DRAWING.ADDED || data.type === DRAWING.DELETED) {
        this.getFileList()
      }
    },
    async fileShare({ data }) {
      if (data.type === DRAWING.FILE_SHARE) {
        if (data.contentType === 'application/pdf') {
          this.$refs['file-list'].addPdfHistory(data)
        } else {
          const res = await drawingDownload({
            sessionId: this.roomInfo.sessionId,
            workspaceId: this.workspace.uuid,
            objectName: data.objectName,
            userId: this.account.uuid,
          })
          //image shareFile(vuex) set되는 곳
          this.addHistory({
            id: Date.now(),
            name: res.name,
            fileName: res.name,
            width: data.width,
            height: data.height,
            img: res.url,
            pageNum: 0, //image인 경우 pageNum은 0으로 보내도록한다.
            objectName: res.objectName,
            contentType: res.contentType,
          })
        }
      }
    },
    onExitDrawing() {
      this.toastDefault(this.$t('service.toast_drawing_end'))
      this.showImage({}) //공유중 파일 초기화
      this.setView(VIEW.STREAM) //탭 실시간 공유로 이동
    },
  },
  created() {
    this.getFileList()
    this.$eventBus.$on(SIGNAL.DRAWING, this.signalDrawing)
    this.$eventBus.$on(SIGNAL.DRAWING_FROM_VUEX, this.fileShare) //vuex 큐에 임시 저장해두었다가 발생시키는 이벤트 리스너
    this.$eventBus.$on(SIGNAL.DRAWING, this.fileShare) //누락 이벤트가 모두 처리된 후 정상적으로 이벤트 직접 수신
  },
  beforeDestroy() {
    this.$eventBus.$off(SIGNAL.DRAWING, this.signalDrawing)
    this.$eventBus.$off(SIGNAL.DRAWING_FROM_VUEX, this.fileShare)
    this.$eventBus.$off(SIGNAL.DRAWING, this.fileShare)
  },
}
</script>

<style lang="scss" scoped>
@import '~assets/style/mixin';

.mobile-service-footer {
  position: absolute;
  bottom: 0;
  z-index: 2;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  width: 100%;
  padding-bottom: 2.8rem;
  background: linear-gradient(rgba(0, 0, 0, 0), rgba(0, 0, 0, 0.4));
  pointer-events: none;

  .footer-tab-container {
    //width: 25.8rem;
    margin-bottom: 1.6rem;

    > button {
      position: relative;
      width: 8.7rem;
      //height: 2.8rem;
      margin: 0 0.3rem;
      padding: 0.4rem;
      color: $new_color_text_main;
      border-radius: 1.4rem;
      pointer-events: all;
      @include fontLevel(75);

      &.active {
        background-color: rgba($new_color_bg_tab_active_rgb, 0.5);
      }
      &.notice::after {
        position: absolute;
        top: 2px;
        right: 10px;
        width: 0.6rem;
        height: 0.6rem;
        background-color: #d9333a;
        border-radius: 50%;
        content: '';
      }
      &.invisible {
        display: none;
      }
    }
  }

  .footer-button-container {
    display: flex;
    height: 6rem;
    > button,
    > span {
      pointer-events: all;
    }
  }
  .footer-modal-container {
    pointer-events: all;
  }
}
</style>
