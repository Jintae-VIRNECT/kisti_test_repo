import { getDailyCollabo, getMonthlyCollabo } from 'api/http/chart'
import dayjs from 'dayjs'

export const hourLabels = [
  '00',
  '01',
  '02',
  '03',
  '04',
  '05',
  '06',
  '07',
  '08',
  '09',
  '10',
  '11',
  '12',
  '13',
  '14',
  '15',
  '16',
  '17',
  '18',
  '19',
  '20',
  '21',
  '22',
  '23',
]

export const getDays = date => {
  const dayCount = dayjs(date).daysInMonth()
  const days = []
  for (let i = 1; i <= dayCount; i++) {
    days.push(i)
  }
  return days
}

export const getDailyData = async ({ workspaceId, userId, selectedDate }) => {
  const result = {
    total: {},
    my: {},
  }

  selectedDate = dayjs(selectedDate).format('YYYY-MM-DD')
  const timeDifference = new Date().getTimezoneOffset()

  let response = await getDailyCollabo({
    workspaceId,
    userId,
    selectedDate,
    timeDifference,
  })

  result.total.time = response.entireDuration.reduce(sum, 0)
  result.total.count = response.entireHistory.reduce(sum, 0)
  result.total.set = response.entireHistory

  result.my.time = response.myDuration.reduce(sum, 0)
  result.my.count = response.myHistory.reduce(sum, 0)
  result.my.set = response.myHistory

  return result
}

export const getMonthlyData = async ({ workspaceId, userId, date }) => {
  const result = {
    total: {},
    my: {},
  }
  let selectedMonth = dayjs(date).format('YYYY-MM')

  const timeDifference = new Date().getTimezoneOffset()
  let response = await getMonthlyCollabo({
    workspaceId,
    userId,
    selectedMonth,
    timeDifference,
  })

  result.total.time = response.entireDuration.reduce(sum, 0)
  result.total.count = response.entireHistory.reduce(sum, 0)
  result.total.set = response.entireHistory

  result.my.time = response.myDuration.reduce(sum, 0)
  result.my.count = response.myHistory.reduce(sum, 0)
  result.my.set = response.myHistory

  return result
}

const sum = (sum, elm) => {
  return sum + elm
}
