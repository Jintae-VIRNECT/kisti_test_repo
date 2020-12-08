
import {Calendar as VCalendar}  from './lib'
import {DatePicker as VDatePicker } from './lib'
import * as VDatePickerPlugin from './lib'

export default {
  install(Vue) {
    Vue.mixin({
      components: {
        VCalendar,
        VDatePicker,
        VDatePickerPlugin
      },
    })
  },
}
