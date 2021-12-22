/**
 * API 결과 메시지 출력 공통 mixin
 */
export default {
  data() {
    return {
      message: {
        800: 'members.setting.message.updateSuccess',
        900: 'members.add.message.memberOverflow',
        1000: 'common.error',
        1002: 'members.add.message.memberAlready',
        1016: 'members.delete.message.wrongPassword',
        1007: 'members.add.message.notHaveAnyPlan',
        1008: 'members.add.message.memberOverflow',
        1011: 'members.create.message.idDuplicated',
        1018: 'members.add.message.memberWithdrawal',
        1021: 'members.setting.message.notChangeMasterPlan',
        2201: 'members.create.message.idDuplicated',
        6000: 'workspace.onpremiseSetting.upload.error.fail',
        6001: 'workspace.onpremiseSetting.upload.error.size',
        6002: 'workspace.onpremiseSetting.upload.error.notExtension',
        6007: 'workspace.onpremiseSetting.upload.error.duplicateVersion',
        6008: 'workspace.onpremiseSetting.upload.error.lowVersion',
      },
    }
  },
  methods: {
    /**
     * 에러 출력
     * @param {object} e 에러객체
     */
    errorMessage(e) {
      this.$message.error({
        message: this.getMessage(e),
        duration: 3000,
        showClose: true,
        dangerouslyUseHTMLString: true,
      })
    },
    /**
     * success message 출력
     * @param {string} msg 출력할 메시지
     */
    successMessage(msg) {
      this.$message.success({
        message: msg,
        duration: 3000,
        showClose: true,
        dangerouslyUseHTMLString: true,
      })
    },
    /**
     * 메세지 리턴 함수
     * @param {Error} e
     * @returns {string} 메시지
     */
    getMessage(e) {
      const code = this.errorCode(e) ? this.errorCode(e) : this.successCode(e)
      const message = this.message[code]
        ? this.$t(this.message[code])
        : `${this.$t(this.message[1000])}<br>(${e})`
      return message
    },
    /**
     * 에러코드 추출 함수
     * @param {Error} e 에러객체
     * @returns {string} 코드번호
     */
    errorCode(e) {
      return e.toString().match(/^Error: ([0-9]+)/)[1]
    },
    /**
     * 성공코드 추출 함수
     * @param {Error} e 에러객체
     * @returns {string} 코드번호
     */
    successCode(e) {
      return e.toString().match(/^Success: ([0-9]+)/)[1]
    },
  },
}
