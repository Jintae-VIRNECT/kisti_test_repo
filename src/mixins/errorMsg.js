import toastMixin from 'mixins/toast'
import { ERROR } from 'configs/error.config'

export default {
  mixins: [toastMixin],
  methods: {
    /**
     *
     * @param {Number} code 에러코드
     */
    showErrorToast(code) {
      //카테고리용 코드
      switch (code) {
        case ERROR.REMOTE_ALREADY_REMOVED:
          this.toastError(this.$t('workspace.remote_already_removed'))
          break
        case ERROR.CONFIRM_REMOTE_LEADER_LEAVE:
          this.toastError(this.$t('workspace.confirm_remote_leader_leave'))
          break
        case ERROR.REMOTE_ALREADY_INVITE:
          this.toastError(this.$t('workspace.remote_already_invite'))
          break
        case ERROR.NO_LICENSE:
          this.toastError(this.$t('workspace.no_license'))
          break
        case ERROR.FILE_DUMMY_ASSUMED:
          this.toastError(this.$t('service.file_dummy_assumed'))
          break
        case ERROR.FILE_STORAGE_CAPACITY_FULL:
          this.toastError(this.$t('alarm.file_storage_capacity_full'))
          break
        case ERROR.FILE_EXTENSION_UNSUPPORT:
          this.toastError(this.$t('service.file_extension_unsupport'))
          break
        case ERROR.FILE_SIZE_EXCEEDED:
          this.toastError(this.$t('service.file_size_exceeded'))
          break
        case ERROR.FILE_ENCRYPTED:
          this.toastError(this.$t('service.encrypted_file_unsupport'))
          break
      }
    },
  },
}
