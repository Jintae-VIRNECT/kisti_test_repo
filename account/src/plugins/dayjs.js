import dayjs from 'dayjs'
import utc from 'dayjs/plugin/utc'

dayjs.extend(utc)

export default dayjs
export const filters = {
  localTimeFormat(param) {
    return dayjs
      .utc(param)
      .local()
      .format('YYYY.MM.DD HH:mm')
  },
  utcTimeFormat(param) {
    return dayjs.utc(param).format('YYYY.MM.DD HH:mm')
  },
}
