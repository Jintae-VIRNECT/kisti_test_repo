import dayjs from 'dayjs'
import duration from 'dayjs/plugin/duration'
import utc from 'dayjs/plugin/utc'
import localizedFormat from 'dayjs/plugin/localizedFormat'

export default {
  install(Vue) {
    dayjs.extend(duration)
    dayjs.extend(utc)
    dayjs.extend(localizedFormat)

    Vue.prototype.$dayjs = dayjs
  },
}
