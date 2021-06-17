// import Swal from 'sweetalert2/dist/sweetalert2.js'
import { Alert, CANCEL, BACKDROP } from 'plugins/remote/sweetalert2'

export default {
  methods: {
    /**
     * 확인
     * @param {String} text
     * @param {Object} confirm { text, action }
     */
    confirmDefault(
      text,
      confirm = { text: this.$t('button.confirm') },
      options = {},
    ) {
      Alert.fire({
        html: text,
        confirmButtonText: confirm.text || this.$t('button.confirm'),
        ...options,
      }).then(result => {
        if (result.value) {
          if (typeof confirm.action === 'function') {
            confirm.action()
          }
        } else if (result.dismiss === BACKDROP) {
          if (typeof confirm.action === 'function') {
            confirm.action()
          }
        }
      })
    },
    /**
     * 취소, 확인
     * @param {String} text
     * @param {Object} confirm { text, action }
     * @param {Object} cancel { text, action }
     */
    confirmCancel(
      text,
      confirm = { text: this.$t('button.confirm') },
      cancel = { text: this.$t('button.cancel') },
      options = {},
    ) {
      Alert.fire({
        html: text,
        showCancelButton: true,
        confirmButtonText: confirm.text,
        cancelButtonText: cancel.text,
        ...options,
      }).then(result => {
        if (result.value) {
          if (typeof confirm.action === 'function') {
            confirm.action()
          }
        } else if (result.dismiss === CANCEL) {
          if (typeof cancel.action === 'function') {
            cancel.action()
          }
        } else if (result.dismiss === BACKDROP) {
          if (cancel.backdrop === true) {
            return
          }
          if (typeof cancel.action === 'function') {
            cancel.action()
          }
        }
      })
    },
    /**
     * 취소, 확인
     * @param {String} text
     * @param {Object} confirm { text, action }
     * @param {Object} cancel { text, action }
     */
    serviceConfirmCancel(
      text,
      confirm = { text: this.$t('button.confirm') },
      cancel = { text: this.$t('button.cancel') },
    ) {
      Alert.fire({
        html: text,
        customClass: 'service-confirm',
        showCancelButton: true,
        confirmButtonText: confirm.text,
        cancelButtonText: cancel.text,
      }).then(result => {
        if (result.value) {
          if (typeof confirm.action === 'function') {
            confirm.action()
          }
        } else if (result.dismiss === CANCEL) {
          if (typeof cancel.action === 'function') {
            cancel.action()
          }
        } else if (result.dismiss === BACKDROP) {
          if (cancel.backdrop === true) {
            return
          }
          if (typeof cancel.action === 'function') {
            cancel.action()
          }
        }
      })
    },
    /**
     * 취소, 확인
     * @param {String} title
     * @param {String} text
     * @param {Object} confirm { text, action }
     * @param {Object} cancel { text, action }
     */
    serviceConfirmTitle(
      title,
      text,
      confirm = { text: this.$t('button.confirm') },
      cancel = { text: this.$t('button.cancel') },
    ) {
      Alert.fire({
        title: title,
        html: text,
        customClass: 'service-confirm',
        showCancelButton: true,
        confirmButtonText: confirm.text,
        cancelButtonText: cancel.text,
      }).then(result => {
        if (result.value) {
          if (typeof confirm.action === 'function') {
            confirm.action()
          }
        } else if (result.dismiss === CANCEL) {
          if (typeof cancel.action === 'function') {
            cancel.action()
          }
        } else if (result.dismiss === BACKDROP) {
          if (cancel.backdrop === true) {
            return
          }
          if (typeof cancel.action === 'function') {
            cancel.action()
          }
        }
      })
    },
    confirmClose() {
      return Alert.close()
    },
  },
}
