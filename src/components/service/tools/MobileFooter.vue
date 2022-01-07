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
        <span>{{ $t(menu.text) }}</span>
      </button>
    </div>
    <div class="footer-button-container">
      <!-- 실시간 공유 -->
      <template v-if="view === VIEW.STREAM">
        <mobile-more-button
          @selectMember="openParticipantModal"
        ></mobile-more-button>
        <mobile-capture-button
          v-if="isLeader"
          :disabled="!isMainViewOn"
        ></mobile-capture-button>
        <mobile-flash-button></mobile-flash-button>
      </template>

      <!-- 협업보드 -->
      <template v-else-if="view === VIEW.DRAWING">
        <!-- 협업보드 종료 버튼 리더에게만 표시 -->
        <mobile-drawing-exit-button
          v-if="historyList.length > 0 && isLeader"
          @exitDrawing="onExitDrawing"
        ></mobile-drawing-exit-button>
        <mobile-file-list-button
          :disabled="fileList.length === 0 && uploadingFile === ''"
          @openFileListModal="openFileListModal"
          :notice="fileListNotice"
        ></mobile-file-list-button>
        <mobile-download-button
          :disabled="historyList.length === 0"
        ></mobile-download-button>
        <mobile-upload-button
          @uploading="onFileUploading"
          @uploaded="onFileUploaded"
          @uploadFailed="onFileUploadFailed"
        ></mobile-upload-button>
      </template>

      <!-- ar 공유 & 3d 공유 -->
      <template v-else-if="view === VIEW.AR && viewAction === ACTION.AR_3D">
        <mobile-file-list-button
          :disabled="ar3dFileList.length === 0 && uploadingFile === ''"
          @openFileListModal="open3dFileListModal"
        ></mobile-file-list-button>
        <mobile-upload-button
          :fileType="FILE_TYPE.OBJECT"
          @uploading="onFileUploading"
          @uploaded="on3dFileUploaded"
          @uploadFailed="onFileUploadFailed"
        ></mobile-upload-button>
      </template>
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

import { remoteFileList, remoteFileDownload } from 'api/http/drawing'
import { mapGetters, mapActions } from 'vuex'
import { VIEW, ACTION } from 'configs/view.config'
import { SIGNAL, DRAWING, ROLE, FILE_TYPE } from 'configs/remote.config'

export default {
  name: 'MobileFooter',
  mixins: [tabChangeMixin, fileShareEventQueueMixin],
  components: {
    MobileMoreButton,
    MobileCaptureButton,
    MobileFlashButton,
    MobileUploadButton,
    MobileFileListButton,
    MobileDownloadButton,
    MobileDrawingExitButton,
  },
  props: {
    fetchedFileList: {
      type: Array,
    },
    fetchedAr3dFileList: {
      type: Array,
    },
  },
  data() {
    return {
      FILE_TYPE: Object.freeze(FILE_TYPE),
      ACTION: Object.freeze(ACTION),
      VIEW: Object.freeze(VIEW),
      fileList: [],
      ar3dFileList: [],
      fileListNotice: false,
      uploadingFile: '',
    }
  },
  computed: {
    ...mapGetters([
      'mainView',
      'historyList',
      'roomInfo',
      'myInfo',
      'viewAction',
    ]),
    isMainViewOn() {
      return this.mainView && this.mainView.id && this.mainView.video
    },
    isLeader() {
      return this.account.roleType === ROLE.LEADER
    },
  },
  watch: {
    viewAction: {
      immediate: true,
      handler(newVal) {
        if (newVal === ACTION.AR_3D) {
          this.get3dFileList()
        }
      },
    },
    fetchedFileList(newVal) {
      this.fileList = newVal
    },
    fetchedAr3dFileList(newVal) {
      this.ar3dFileList = newVal
    },
  },
  methods: {
    ...mapActions(['addHistory']),
    openParticipantModal() {
      this.$emit('participantModalShow')
    },
    openFileListModal() {
      this.fileListNotice = false
      this.$emit('openFileListModal')
    },
    open3dFileListModal() {
      this.$emit('open3dFileListModal')
    },
    onFileUploaded() {
      this.uploadingFile = ''
      this.$emit('uploaded')
      this.getFileList()
    },
    onFileUploadFailed() {
      this.uploadingFile = ''
      this.$emit('uploadFailed')
    },
    //협업보드 파일 목록 조회
    async getFileList() {
      const res = await remoteFileList({
        fileType: FILE_TYPE.SHARE,
        sessionId: this.roomInfo.sessionId,
        workspaceId: this.workspace.uuid,
      })
      this.fileList = res.fileInfoList
      this.$emit('getFileList', res.fileInfoList)
    },
    onFileUploading(fileName) {
      this.uploadingFile = fileName
      this.$emit('uploading', this.uploadingFile)
    },
    on3dFileUploaded() {
      this.uploadingFile = ''
      this.$emit('uploaded')
      this.get3dFileList()
    },
    //ar 3d 공유 파일 목록 조회
    async get3dFileList() {
      const res = await remoteFileList({
        fileType: FILE_TYPE.OBJECT,
        sessionId: this.roomInfo.sessionId,
        workspaceId: this.workspace.uuid,
      })
      this.ar3dFileList = res.fileInfoList
      this.$emit('get3dFileList', res.fileInfoList)
    },
    signalDrawing({ data, receive }) {
      if (data.type === DRAWING.ADDED || data.type === DRAWING.DELETED) {
        //타 참가자가 파일 업로드 한 경우 파일목록 버튼에 NOTICE 활성화
        if (
          this.myInfo &&
          receive &&
          this.myInfo.connectionId !== receive.from.connectionId
        ) {
          this.fileListNotice = true
        }

        this.getFileList()
      }
    },
    async fileShare({ data }) {
      if (data.type === DRAWING.FILE_SHARE) {
        if (data.contentType === 'application/pdf') {
          //this.$refs['file-list'].addPdfHistory(data)
          this.$emit('addPdfHistory', data)
        } else {
          const res = await remoteFileDownload({
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
      min-width: 8.7rem;
      //height: 2.8rem;
      margin: 0 0.3rem;
      padding: 0.4rem 1.2rem;
      color: $new_color_text_main;
      border-radius: 1.4rem;
      pointer-events: all;

      span {
        position: relative;
        @include fontLevel(75);
      }

      &.active {
        background-color: rgba($new_color_bg_tab_active_rgb, 0.5);
      }
      &.notice > span::after {
        position: absolute;
        top: -2px;
        right: -9px;
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
