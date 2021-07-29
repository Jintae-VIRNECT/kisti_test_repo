<template>
  <div class="share-body__file">
    <vue2-scrollbar ref="upload-list-scroll">
      <ol class="upload-list" ref="upload-list">
        <li>
          <button class="upload-list__button" @click="addFileClick()">
            {{ $t('service.file_add') }}
          </button>
          <input
            type="file"
            ref="uploadFile"
            style="display:none;"
            accept="image/gif,image/bmp,image/jpeg,image/png,application/pdf"
            @change="fileChangeHandler"
          />
        </li>
        <template v-for="file of sharingList">
          <sharing-pdf
            v-if="file.contentType === 'application/pdf'"
            :ref="'sharing_' + file.objectName"
            :key="'sharing_' + file.objectName"
            :fileInfo="file"
            @pdfView="pdfView(file)"
          ></sharing-pdf>
          <sharing-image
            v-else
            :key="'sharing_' + file.objectName"
            :fileInfo="file"
          ></sharing-image>
        </template>
      </ol>
    </vue2-scrollbar>
  </div>
</template>

<script>
import { mapActions, mapGetters } from 'vuex'
import SharingImage from './SharingImage'
import SharingPdf from './SharingPdf'

import toastMixin from 'mixins/toast'
import errorMsgMixin from 'mixins/errorMsg'

import { drawingUpload, drawingList, drawingDownload } from 'api/http/drawing'

import { SIGNAL, DRAWING } from 'configs/remote.config'
import { ERROR } from 'configs/error.config'

import { isOverflowY } from 'utils/element.js'
import { resetOrientation } from 'utils/file'
import { setQueueAct } from 'plugins/remote/call/RemoteSessionEventListener'

const maxFileSize = 1024 * 1024 * 20
export default {
  name: 'ShareFileList',
  mixins: [toastMixin, errorMsgMixin],
  components: {
    SharingImage,
    SharingPdf,
  },
  data() {
    return {
      sharingList: [],
      cbGetFileList: () => {},
    }
  },
  computed: {
    ...mapGetters(['roomInfo', 'fileShareEventQueue']),
  },
  watch: {
    sharingList(val, oldVal) {
      if (val.length !== oldVal.length) {
        this.$nextTick(() => {
          const isNotOverflow = !isOverflowY(
            this.$refs['upload-list'],
            this.$refs['upload-list-scroll'].$el,
          )
          //overflow 해제 시 scroll을 최상위로 원위치
          if (isNotOverflow) this.$refs['upload-list-scroll'].scrollToY(0)
        })
      }
    },
    //해당 component 생성 전에 발생했던 이벤트를 vuex에 저장해두고 처리한다.
    fileShareEventQueue: {
      immediate: true,
      handler(newVal) {
        if (newVal.length) {
          //해당 component에 created에서 이벤트버스 리스너가 활성화된 후 이벤트를 발생 시킨다
          this.$nextTick(() => {
            this.$eventBus.$emit(
              SIGNAL.DRAWING_FROM_VUEX,
              this.fileShareEventQueue.shift(), //첫번째 요소부터 실행 시키고, 제거
            )
          })
        } else setQueueAct(false) //큐 안에 데이터를 모두 처리한 후 큐는 비활성화 한다. (정상적으로 시그널데이터를 바로 이벤트로 발생시키도록)
      },
    },
  },
  methods: {
    ...mapActions(['addFile', 'addHistory']),
    pdfView(file) {
      this.$emit('pdfView', {
        id: file.objectName,
        name: file.name,
        objectName: file.objectName,
        contentType: file.contentType,
      })
    },
    addFileClick(file) {
      if (file) {
        this.loadFile(file)
      } else {
        this.$refs['uploadFile'].click()
      }
    },
    fileChangeHandler(event) {
      const file = event.target.files[0]
      this.loadFile(file)
    },
    async getFileList() {
      const res = await drawingList({
        sessionId: this.roomInfo.sessionId,
        workspaceId: this.workspace.uuid,
      })
      this.sharingList = res.fileInfoList
      this.$nextTick(() => {
        this.cbGetFileList()
      })
    },
    async loadFile(file) {
      if (file) {
        if (file.size > maxFileSize) {
          this.toastError(this.$t('service.file_maxsize'))
          this.clearUploadFile()
          return false
        }

        const isAcceptable = [
          'image/jpeg',
          'image/png',
          'image/bmp',
          'image/gif',
          'application/pdf',
        ].includes(file.type)

        let res = null

        if (isAcceptable) {
          //image의 경우 orientation 교정 실행
          if (
            ['image/jpeg', 'image/png', 'image/bmp', 'image/gif'].includes(
              file.type,
            )
          ) {
            const resetedFile = await resetOrientation(file)
            if (resetedFile) file = resetedFile
          }

          try {
            res = await drawingUpload({
              file: file,
              sessionId: this.roomInfo.sessionId,
              userId: this.account.uuid,
              workspaceId: this.workspace.uuid,
            })

            if (res.usedStoragePer >= 90) {
              this.toastError(this.$t('alarm.file_storage_about_to_limit'))
            } else {
              this.toastDefault(this.$t('alarm.file_uploaded'))
            }
          } catch (err) {
            switch (err.code) {
              case ERROR.FILE_EXTENSION_UNSUPPORT: //미지원 파일 확장자
              case ERROR.FILE_STORAGE_CAPACITY_FULL: //파일 스토리지 용량 초과
              case ERROR.FILE_ENCRYPTED: //암호화 파일
                this.showErrorToast(err.code)
                break
              default:
                this.toastError(this.$t('confirm.network_error'))
            }
            return false
          }

          this.$call.sendDrawing(DRAWING.ADDED, {
            deleted: false, //false
            expired: false, //false
            sessionId: res.sesssionId,
            name: res.name,
            objectName: res.objectName,
            contentType: res.contentType, // "image/jpeg", "image/bmp", "image/gif", "application/pdf",
            size: res.size,
            createdDate: res.createdDate,
            expirationDate: res.expirationDate,
            width: res.width, //pdf 는 0
            height: res.height,
          })
          this.clearUploadFile()
          this.getFileList()
        } else {
          this.toastError(this.$t('service.file_type'))
          return false
        }
      }
    },
    clearUploadFile() {
      this.$refs['uploadFile'].value = ''
    },
    loadPdf(data) {
      if (this.sharingList.length === 0) {
        this.cbGetFileList = () => {
          this.cbGetFileList = () => {}
          this.$refs['sharing_' + data.objectName][0].addPdfHistory(
            data.index + 1,
          )
        }
        return
      }
      this.$refs['sharing_' + data.objectName][0].addPdfHistory(data.index + 1)
    },

    async fileShare({ data }) {
      if (data.type === DRAWING.FILE_SHARE) {
        if (data.contentType === 'application/pdf') {
          this.loadPdf(data)
          // this.$eventBus.$emit(`loadPdf_${data.objectName}`, data.index + 1)
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
  },

  /* Lifecycles */
  created() {
    this.$eventBus.$on('addFile', this.addFileClick)
    this.$eventBus.$on(SIGNAL.DRAWING_FROM_VUEX, this.fileShare) //vuex 큐에 임시 저장해두었다가 발생시키는 이벤트 리스너
    this.$eventBus.$on(SIGNAL.DRAWING, this.fileShare) //누락 이벤트가 모두 처리된 후 정상적으로 이벤트 직접 수신
    this.getFileList()
  },
  beforeDestroy() {
    this.$eventBus.$off('addFile', this.addFileClick)
    this.$eventBus.$off(SIGNAL.DRAWING_FROM_VUEX, this.fileShare)
    this.$eventBus.$off(SIGNAL.DRAWING, this.fileShare)
  },
  destroyed() {
    //해당 component가 제거되면 다시 queue를 활성화 시키도록한다.
    setQueueAct(true)
  },
}
</script>
