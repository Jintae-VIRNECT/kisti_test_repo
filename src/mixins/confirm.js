// import Swal from 'sweetalert2/dist/sweetalert2.js'
import { Alert, CANCEL, BACKDROP } from 'plugins/remote/sweetalert2'

export default {
  methods: {
    /**
     * 확인
     * @param {String} text
     * @param {Object} confirm { text, action }
     */
    confirmDefault(text, confirm = { text: '확인' }) {
      Alert.fire({
        text: text,
        confirmButtonText: confirm.text,
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
    confirmCancel(text, confirm = { text: '확인' }, cancel = { text: '취소' }) {
      Alert.fire({
        text: text,
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
          if (typeof cancel.action === 'function') {
            cancel.action()
          }
        }
      })
    },
  },
}
