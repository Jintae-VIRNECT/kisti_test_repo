import dayjs from 'dayjs'
import duration from 'dayjs/plugin/duration'
import utc from 'dayjs/plugin/utc'
import localizedFormat from 'dayjs/plugin/localizedFormat'
import calendar from 'dayjs/plugin/calendar'
import 'dayjs/locale/ko'
import 'dayjs/locale/ja'
import 'dayjs/locale/es'
import 'dayjs/locale/zh-cn'
import 'dayjs/locale/zh-tw'

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
