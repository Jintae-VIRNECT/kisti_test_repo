import dayjs from 'dayjs'
import utc from 'dayjs/plugin/utc'

dayjs.extend(utc)

export default {
  _: dayjs,
  filters: {
    dayJs_FilterDateTimeFormat(param) {
      return dayjs
        .utc(param)
        .local()
        .format('YYYY.MM.DD HH:mm')
    },
    dayJS_ConvertLocalTime(param) {
      return dayjs.utc(param).local()
    },
    dayJS_ConvertUTCTimeFormat(param) {
      return dayjs.utc(param).format()
    },
  },
}
