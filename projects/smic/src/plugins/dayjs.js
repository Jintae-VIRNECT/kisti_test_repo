import dayjs from 'dayjs'
import utc from 'dayjs/plugin/utc'

dayjs.extend(utc)

export default {
  filters: {
    dayJs_FilterDateTime(param) {
      return dayjs
        .utc(param)
        .local()
        .format('YYYY.MM.DD HH:mm')
    },
  },
}
