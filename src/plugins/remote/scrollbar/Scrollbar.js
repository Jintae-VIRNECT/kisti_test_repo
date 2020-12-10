import Vue2Scrollbar from './components/vue-scrollbar.vue'
import './style/vue2-scrollbar.scss'

export default {
  install(Vue) {
    Vue.mixin({
      components: {
        Vue2Scrollbar,
      },
    })
  },
}
