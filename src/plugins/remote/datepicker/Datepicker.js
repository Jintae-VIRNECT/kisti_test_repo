import VueDatepicker from './components/Datepicker.vue'

export default {
  install(Vue) {
    Vue.mixin({
      components: {
        VueDatepicker,
      },
    })
  },
}
