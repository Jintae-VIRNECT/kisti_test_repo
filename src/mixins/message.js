/**
 * API 결과 메시지 출력 공통 mixin
 */
export default {
  data() {
    return {
      message: {
        800: this.$t('members.setting.message.updateSuccess'),
        900: this.$t('members.add.message.memberOverflow'),
        1000: this.$t('common.error'),
        1002: this.$t('members.add.message.memberAlready'),
        1003: this.$t('members.delete.message.wrongPassword'),
        1007: this.$t('members.add.message.notHaveAnyPlan'),
        1008: this.$t('members.add.message.memberOverflow'),
        1011: this.$t('members.create.message.idDuplicated'),
        1018: this.$t('members.add.message.memberWithdrawal'),
        1021: this.$t('members.setting.message.notChangeMasterPlan'),
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
        ? this.message[code]
        : this.message[1000] + `<br>(${e})`
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
