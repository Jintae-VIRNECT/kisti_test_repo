import dayjs from 'dayjs'
import duration from 'dayjs/plugin/duration'
import utc from 'dayjs/plugin/utc'
import localizedFormat from 'dayjs/plugin/localizedFormat'
import calendar from 'dayjs/plugin/calendar'
import 'dayjs/locale/ko'

export default {
  install(Vue) {
    dayjs.extend(duration)
    dayjs.extend(utc)
    dayjs.extend(localizedFormat)
    dayjs.extend(calendar)

    dayjs.locale('ko')

    Vue.prototype.$dayjs = dayjs
  },
}
