import dayjs from 'dayjs'

export const dateTimeFormat = dateTime => {
  return dayjs(dateTime + '+00:00').format('YYYY.MM.DD HH:mm')
}

export const durationFormat = time => {
  return dayjs(time * 1000)
    .utc()
    .format('HH:mm')
}

export const timeFormat = time => {
  return dayjs(time + '+00:00').format('HH:mm:ss')
}

export const dateFormat = date => {
  return dayjs(date + '+00:00').format('YYYY.MM.DD')
}
