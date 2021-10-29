import { DRAWING } from 'configs/remote.config'
import { ERROR } from 'configs/error.config'
import { resetOrientation } from 'utils/file'
import { drawingUpload } from 'api/http/drawing'
import toastMixin from 'mixins/toast'
import errorMsgMixin from 'mixins/errorMsg'
import { mapGetters } from 'vuex'

const maxFileSize = 1024 * 1024 * 20

//모바일, PC 협업보드 파일 업로드 관련 메서드를 공통으로 사용한다.

export default {
  mixins: [toastMixin, errorMsgMixin],
  data() {
    return {}
  },
  computed: {
    ...mapGetters(['roomInfo']),
  },
  methods: {
    async loadFile(file, callback = () => {}) {
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
                if (err.code) {
                  this.toastError(this.$t('confirm.network_error'))
                }
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
          callback()
        } else {
          this.toastError(this.$t('service.file_type'))
          return false
        }
      }
    },
    clearUploadFile() {
      this.$refs['uploadFile'].value = ''
    },
  },
}
