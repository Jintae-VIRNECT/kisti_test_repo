import dayjs from 'dayjs'
import utc from 'dayjs/plugin/utc'

/**
 * 비정상 날짜 예외처리
 * @param {String} paramStr
 */
function valid(paramStr) {
  if (!paramStr) return false
  if (paramStr === '1500-01-01T00:00:00') return false
  return true
}

dayjs.extend(utc)
export default dayjs
export const filters = {
  dateFormat(param) {
    if (!valid(param)) return null
    return dayjs(param).format('YY.MM.DD')
  },
  localTimeFormat(param) {
    if (!valid(param)) return null
    return dayjs
      .utc(param)
      .local()
      .format('YY.MM.DD HH:mm')
  },
  localDateFormat(param) {
    if (!valid(param)) return null
    return dayjs
      .utc(param)
      .local()
      .format('YY.MM.DD')
  },
  utcTimeFormat(param) {
    if (!valid(param)) return null
    return dayjs.utc(param).format('YY.MM.DD HH:mm')
  },
  utcDateFormat(param) {
    if (!valid(param)) return null
    return dayjs.utc(param).format('YY.MM.DD')
  },
}
