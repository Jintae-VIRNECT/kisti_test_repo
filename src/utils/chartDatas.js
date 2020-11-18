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

export const getDailyData = async ({ workspaceId, userId, date }) => {
  const result = {
    total: {},
    my: {},
  }

  date = dayjs(date).format('YYYY-MM-DD')
  const time = new Date().getTimezoneOffset()

  let response = await getDailyCollabo({ workspaceId, userId, date, time })

  result.total.time = response.entireDuration.reduce(sum, 0)
  result.total.count = response.entireHistory.reduce(sum, 0)
  result.total.set = response.entireHistory

  result.my.time = response.myDuration.reduce(sum, 0)
  result.my.count = response.myHistory.reduce(sum, 0)
  result.my.set = response.myHistory

  console.log('result::', result)
  return result
}

export const getMonthlyData = async ({ workspaceId, userId, date }) => {
  const result = {
    total: {},
    my: {},
  }
  let month = dayjs(date).format('YYYY-MM')

  const time = new Date().getTimezoneOffset()
  let response = await getMonthlyCollabo({ workspaceId, userId, month, time })

  result.total.time = response.entireDuration.reduce(sum, 0)
  result.total.count = response.entireHistory.reduce(sum, 0)
  result.total.set = response.entireHistory

  result.my.time = response.myDuration.reduce(sum, 0)
  result.my.count = response.myHistory.reduce(sum, 0)
  result.my.set = response.myHistory
  console.log('monthly::', result)
  return result
}

const sum = (sum, elm) => {
  return sum + elm
}
