import Swal from 'sweetalert2/dist/sweetalert2.js'
import './style/sweetalert2.scss'

export default {
  install(Vue) {
    Vue.prototype.$alert = Swal
  },
}
