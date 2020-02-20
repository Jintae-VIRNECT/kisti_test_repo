import dayjs from 'dayjs'
import utc from 'dayjs/plugin/utc'

dayjs.extend(utc)

export default {
  _: dayjs,
  filters: {
    dayJs_FilterDateTime(param) {
      return dayjs
        .utc(param)
        .local()
        .format('YYYY.MM.DD HH:mm')
    },
    dayJS_ConvertUTCTime(param) {
      return dayjs.utc(param).format()
    },
  },
}
