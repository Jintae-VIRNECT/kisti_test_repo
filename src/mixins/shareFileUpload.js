import { DRAWING, FILE_TYPE, ACCEPTABLE_FILE_TYPE } from 'configs/remote.config'
import { ERROR } from 'configs/error.config'
import { resetOrientation } from 'utils/file'
import { remoteFileUpload } from 'api/http/drawing'
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
    //협업보드, ar 3d공유 파일 업로드 함수 공통 사용
    async loadFile(
      file,
      callback = () => {},
      fileType = FILE_TYPE.SHARE,
      failedCallback = () => {},
    ) {
      if (file) {
        if (file.size > maxFileSize) {
          this.toastError(this.$t('service.file_maxsize'))
          this.clearUploadFile()
          failedCallback()
          return false
        }

        this.uploadingFileName = file.name //업로드 스피너 실행

        const attachedFileType = this.getAttachedFileType(file, fileType) //첨부된 파일 확장자 가져오기
        const isAcceptable = ACCEPTABLE_FILE_TYPE[fileType].includes(
          attachedFileType,
        )

        let res = null

        if (isAcceptable) {
          //협업보드 image의 경우 orientation 교정 실행 후
          const resultFile = await this.resetImageOrientation(
            file,
            fileType,
            attachedFileType,
          )

          //업로드하게 될 최종 file 객체
          const uploadFile = resultFile || file

          try {
            //업로드 api 호출
            res = await remoteFileUpload({
              file: uploadFile,
              fileType,
              sessionId: this.roomInfo.sessionId,
              userId: this.account.uuid,
              workspaceId: this.workspace.uuid,
            })

            this.removeUploadSpinner()

            //저장 용량 경고 체크
            if (res.usedStoragePer >= 90) {
              this.toastError(this.$t('alarm.file_storage_about_to_limit'))
            } else {
              this.toastDefault(this.$t('alarm.file_uploaded'))
            }
          } catch (err) {
            this.removeUploadSpinner()
            this.clearUploadFile()
            failedCallback()

            switch (err.code) {
              case ERROR.FILE_EXTENSION_UNSUPPORT: //미지원 파일 확장자
              case ERROR.FILE_STORAGE_CAPACITY_FULL: //파일 스토리지 용량 초과
              case ERROR.FILE_ENCRYPTED: //암호화 파일
              case ERROR.FILE_EMPTY: //빈파일 업로드
                this.showErrorToast(err.code)
                break
              default:
                if (err.code) {
                  this.toastError(this.$t('confirm.network_error'))
                }
            }
            return false
          }

          this.sendSignal(res, fileType) //fileType 구분하여 시그널 전송

          this.clearUploadFile()
          callback()
        } else {
          this.clearUploadFile()
          failedCallback()
          this.removeUploadSpinner()
          this.toastError(this.$t('service.file_type'))
          return false
        }
      }
    },

    removeUploadSpinner() {
      this.uploadingFileName = '' //업로드 스피너 제거
    },

    clearUploadFile() {
      this.$refs['uploadFile'].value = ''
    },

    //첨부한 파일의 확장자 반환
    getAttachedFileType(file, fileType) {
      //수용 가능한 파일 필터
      let attachedFileType = ''

      //협업보드
      if (fileType === FILE_TYPE.SHARE) {
        attachedFileType = file.type
      }
      //ar 3d 공유
      else if (fileType === FILE_TYPE.OBJECT) {
        const fileNameLength = file.name.length
        const extDot = file.name.lastIndexOf('.')
        attachedFileType = file.name
          .substring(extDot + 1, fileNameLength)
          .toLowerCase()
      }

      return attachedFileType
    },

    //협업보드 이미지 파일 업로드인 경우 orientation reset한 파일 반환
    async resetImageOrientation(file, fileType, attachedFileType) {
      if (
        fileType === FILE_TYPE.SHARE &&
        ['image/jpeg', 'image/png', 'image/bmp', 'image/gif'].includes(
          attachedFileType,
        )
      ) {
        const resetedFile = await resetOrientation(file)
        return resetedFile
      }
      return null
    },
    sendSignal(res, fileType) {
      if (fileType === FILE_TYPE.SHARE) {
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
      } else return
    },
  },
}
