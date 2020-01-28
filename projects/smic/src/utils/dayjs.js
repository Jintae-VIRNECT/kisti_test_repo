import dayjs from 'dayjs'
export default {
  filters: {
    dayJs_FilterDateTime(params) {
      return dayjs(params).format('YYYY.MM.DD HH:mm')
    },
  },
}
