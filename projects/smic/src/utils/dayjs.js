import dayjs from 'dayjs'
export default {
  filters: {
    filterDateTime(params) {
      return dayjs(params).format('YYYY.MM.DD HH:mm')
    },
  },
}
