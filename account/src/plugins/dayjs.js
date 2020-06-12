import dayjs from 'dayjs'
import utc from 'dayjs/plugin/utc'

dayjs.extend(utc)

export default dayjs
export const filters = {
  dateFormat(param) {
    return dayjs(param).format('YY.MM.DD')
  },
  fullYearDateFormat(param) {
    return dayjs(param).format('YYYY.MM.DD')
  },
  localTimeFormat(param) {
    return dayjs
      .utc(param)
      .local()
      .format('YYYY.MM.DD HH:mm')
  },
  localDateFormat(param) {
    return dayjs
      .utc(param)
      .local()
      .format('YY.MM.DD')
  },
  utcTimeFormat(param) {
    return dayjs.utc(param).format('YYYY.MM.DD HH:mm')
  },
  utcDateFormat(param) {
    return dayjs.utc(param).format('YY.MM.DD')
  },
}
