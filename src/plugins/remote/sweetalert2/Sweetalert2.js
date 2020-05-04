import Swal from 'sweetalert2/dist/sweetalert2.js'
import './style/sweetalert2.scss'

// export default {
//   install(Vue) {
//     Vue.prototype.$alert = Swal
//   },
// }
export const Alert = Swal.mixin({
  reverseButtons: true,
  // customClass: {
  //   cancelButton: 'btn',
  //   confirmButton: 'btn',
  // },
})

export const CANCEL = Swal.DismissReason.cancel
export const BACKDROP = Swal.DismissReason.backdrop
