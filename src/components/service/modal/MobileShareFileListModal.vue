<template>
  <full-screen-modal
    v-show="modalShow"
    :visible="true"
    :title="$t('service.file_list')"
    @close="close"
  >
    <transition name="mobile-file-list__left">
      <ul v-show="!pdfFile || !pdfFile.id" class="mobile-file-list">
        <template v-for="file of fileList">
          <sharing-pdf
            v-if="file.contentType === 'application/pdf'"
            :ref="'sharing_' + file.objectName"
            :key="'sharing_' + file.objectName"
            :fileInfo="file"
            @pdfView="pdfView(file)"
          ></sharing-pdf>
          <sharing-image
            v-else-if="!checkIs3d(file.name)"
            :key="'sharing_' + file.objectName"
            :fileInfo="file"
            @shareImage="shareImage"
          ></sharing-image>
          <sharing-3d-object
            v-else
            :key="'sharing_3d_' + file.objectName"
            :shared="isShareStart"
            :fileInfo="file"
            @3dFileListUpdate="on3dFileListUpdated"
            @get3dFileList="get3dFileList"
          ></sharing-3d-object>
        </template>
        <sharing-file-spinner
          v-if="uploadingFile"
          :fileName="uploadingFile"
        ></sharing-file-spinner>
      </ul>
    </transition>
    <transition name="mobile-file-list__right">
      <pdf-view
        v-show="pdfFile && pdfFile.id"
        :file="pdfFile"
        @back="backToFileList"
      ></pdf-view>
    </transition>
  </full-screen-modal>
</template>

<script>
import SharingImage from '../share/partials/SharingImage'
import SharingPdf from '../share/partials/SharingPdf'
import Sharing3dObject from '../ar/3dcontents/Sharing3dObject'
import PdfView from '../share/partials/SharePdfView'
import FullScreenModal from 'FullScreenModal'
import toastMixin from 'mixins/toast'

import {
  SIGNAL,
  AR_3D_CONTENT_SHARE,
  AR_3D_FILE_SHARE_STATUS,
  FILE_TYPE,
  ACCEPTABLE_FILE_TYPE,
} from 'configs/remote.config'
import { mapGetters, mapMutations } from 'vuex'
import SharingFileSpinner from '../share/partials/SharingFileSpinner.vue'
import { remoteFileList } from 'api/http/drawing'

export default {
  name: 'MobileShareFileListModal',
  mixins: [toastMixin],
  components: {
    FullScreenModal,
    SharingImage,
    SharingPdf,
    Sharing3dObject,
    PdfView,
    SharingFileSpinner,
  },
  props: {
    modalShow: {
      type: Boolean,
      dafault: true,
    },
    uploadingFile: {
      type: String,
    },
    fileList: {
      type: Array,
      default: () => [],
    },
  },
  data() {
    return {
      pdfFile: {},
      isShareStart: false,
    }
  },
  computed: {
    ...mapGetters(['ar3dShareStatus', 'share3dContent', 'roomInfo']),
  },
  watch: {
    fileList: {
      immediate: true,
      handler(newVal, oldVal) {
        //fileList가 조회되고, pdf 목록과 dom이 모두 생성된 후에 부모에 있는 callback을 실행하기 위해 이벤트 전달
        if (newVal.length > 0) {
          this.$nextTick(() => {
            this.$emit('rendered')
          })
        }
        //file목록의 변화가 생긴 결과가 목록이 빈 경우는 자동으로 파일목록 창을 닫는다.
        else if (oldVal.length > 0 && newVal.length === 0 && this.modalShow) {
          this.close()
        }
      },
    },
    ar3dShareStatus: {
      immediate: true,
      handler(newVal) {
        //3d 파일 공유가 시작된 상태인지 여부(모델의 랜더링 시작, 완료 상태)
        const is3dShareStarted =
          newVal === AR_3D_FILE_SHARE_STATUS.START ||
          newVal === AR_3D_FILE_SHARE_STATUS.COMPLETE

        //3d 파일 공유가 랜더링 실패 혹은 취소로 공유상태가 아닌 경우
        const is3dShareCanceled =
          newVal === AR_3D_FILE_SHARE_STATUS.CANCEL ||
          newVal === AR_3D_FILE_SHARE_STATUS.ERROR

        if (is3dShareStarted) {
          this.isShareStart = true
        } else if (is3dShareCanceled) {
          this.isShareStart = false
          this.SHOW_3D_CONTENT({})
          //출력 취소 문구 출력
          this.toastDefault(this.$t('service.ar_3d_load_cancel'))
        }
        //초기상태
        else if (newVal === '') {
          this.isShareStart = false
        }
      },
    },
    share3dContent(newVal) {
      if (newVal) this.close()
    },
  },
  methods: {
    checkIs3d(fileName) {
      const ext = this.getFileExt(fileName)
      if (ACCEPTABLE_FILE_TYPE.OBJECT.includes(ext)) return true
      else return false
    },
    getFileExt(fileName) {
      const fileNameLength = fileName.length
      const extDot = fileName.lastIndexOf('.')
      const attachedFileType = fileName
        .substring(extDot + 1, fileNameLength)
        .toLowerCase()

      return attachedFileType
    },
    ...mapMutations(['SHOW_3D_CONTENT', 'SET_AR_3D_SHARE_STATUS']),
    close() {
      this.$emit('update:modalShow', false)
    },
    shareImage() {
      this.close()
    },
    pdfView(file) {
      this.pdfFile = {
        id: file.objectName,
        name: file.name,
        objectName: file.objectName,
        contentType: file.contentType,
      }
    },
    backToFileList() {
      this.pdfFile = {}
    },
    addPdfHistory(data) {
      this.$refs['sharing_' + data.objectName][0].addPdfHistory(data.index + 1)
      this.backToFileList()
      this.close()
    },

    on3dFileListUpdated(fileList) {
      this.$emit('3dFileListUpdate', fileList)
    },

    async get3dFileList() {
      //3D 파일 목록 조회 API 호출
      const res = await remoteFileList({
        fileType: FILE_TYPE.OBJECT,
        sessionId: this.roomInfo.sessionId,
        workspaceId: this.workspace.uuid,
      })

      this.on3dFileListUpdated(res.fileInfoList)
    },

    //3D 파일 공유 : 랜더링하는 참가자로부터 상태 수신 : start, cancel, complete, error
    setFileShareStatus(event) {
      if (!event.data) return
      const { status, type } = JSON.parse(event.data)
      if (type !== AR_3D_CONTENT_SHARE.FILE_SHARE) return

      this.SET_AR_3D_SHARE_STATUS(status)
      /*
      AR_3D_FILE_SHARE_STATUS.START:
        -선택했던/공유했던 3D 파일 활성화
        -로딩화면 표출
        -툴, 탭 비활성화
      AR_3D_FILE_SHARE_STATUS.CANCEL:
        -선택했던/공유했던 3D 파일 비활성화
        -툴, 탭 활성화
      AR_3D_FILE_SHARE_STATUS.COMPLETE:
        -로딩화면 제거
        -툴, 탭 활성화
      AR_3D_FILE_SHARE_STATUS.ERROR:
        -선택했던/공유했던 3D 파일 비활성화
        -툴, 탭 활성화
      */
    },
  },
  created() {
    this.$eventBus.$on(SIGNAL.AR_3D, this.setFileShareStatus)
  },
  beforeDestroy() {
    this.$eventBus.$off(SIGNAL.AR_3D, this.setFileShareStatus)
  },
}
</script>

<style lang="scss" scoped>
.mobile-file-list {
  display: grid;
  grid-template-columns: 1fr 1fr;
  height: auto;
  padding: 1.8rem 1.6rem;
  column-gap: 0.2rem;
  row-gap: 1.3rem;
}

.mobile-file-list__left-enter-active,
.mobile-file-list__left-leave-active,
.mobile-file-list__right-enter-active,
.mobile-file-list__right-leave-active {
  transition: all ease 0.4s;
}
.mobile-file-list__left-enter {
  left: -100%;
  opacity: 1;
}
.mobile-file-list__left-enter-to {
  left: 0;
  opacity: 1;
}
.mobile-file-list__left-leave {
  left: 0;
  opacity: 1;
}
.mobile-file-list__left-leave-to {
  left: -100%;
  opacity: 0;
}
.mobile-file-list__right-enter {
  left: 100%;
  opacity: 0;
}
.mobile-file-list__right-enter-to {
  left: 0;
  opacity: 1;
}
.mobile-file-list__right-leave {
  left: 0;
  opacity: 1;
}
.mobile-file-list__right-leave-to {
  left: 100%;
  opacity: 0;
}
</style>
