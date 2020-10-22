import { getHistoryList } from 'api/http/history'
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
  console.log({ workspaceId, userId, date })
  const result = {
    total: {},
    my: {},
  }

  //@To-do : 전체 일별 협업수, 전체 일별 협업 시간 불러와야함.
  result.my = await getMyDailyData({ workspaceId, userId, date })
  // result.total = await getTotalDailyData({ workspaceId, userId, date })

  return result
}

export const getMonthlyData = async ({ workspaceId, userId, date }) => {
  console.log({ workspaceId, userId, date })
  const result = {
    total: {},
    my: {},
  }

  //@To-do : 전체 월별 협업수, 전체 월별 협업 시간 불러와야함.
  result.my = await getMyMonthlyData({ workspaceId, userId, date })
  // result.total = await getTotalMonthlyData({ workspaceId, userId, date })

  return result
}

/**
 * 개인 일별 데이터
 * @param {*} param0
 */
const getMyDailyData = async ({ workspaceId, userId, date }) => {
  console.log({ workspaceId, userId, date })

  const result = {}

  const timeMap = new Map()

  hourLabels.forEach(hour => {
    timeMap.set(hour, 0)
  })

  console.log(workspaceId, userId, date)
  const targetDate = dayjs(date)
  const datas = await getHistoryList({
    page: 0,
    paging: false,
    size: 1,
    sort: 'createdDate,desc',
    userId: userId,
    workspaceId: workspaceId,
  })

  const forHour = datas.roomHistoryInfoList.filter(history => {
    const activeDate = dayjs(history.activeDate)
    const sameDay = dayjs(activeDate).isSame(targetDate, 'day')
    if (sameDay) {
      const hour = dayjs(history.activeDate + '+00:00')
        .utc()
        .local()
        .hour()
      timeMap.set(
        hour >= 10 ? hour.toString() : '0' + hour.toString(),
        timeMap.get(hour >= 10 ? hour.toString() : '0' + hour.toString()) + 1,
      )
      return history
    }
  })
  //일일협업 개인 협업 내역 차트 데이터
  result.count = forHour.length
  result.time = forHour.reduce(sumOfTime, 0)
  result.set = [...timeMap.values()]

  return result
}

/**
 * 개인 월별 데이터
 * @param {*} param0
 */
const getMyMonthlyData = async ({ workspaceId, userId, date }) => {
  console.log({ workspaceId, userId, date })

  const result = {}

  const targetDate = dayjs(date)
  const datas = await getHistoryList({
    page: 0,
    paging: false,
    size: 1,
    sort: 'createdDate,desc',
    userId: userId,
    workspaceId: workspaceId,
  })

  const dayMap = new Map()
  const days = getDays(date)
  days.forEach(day => {
    dayMap.set(day, 0)
  })

  const forDay = datas.roomHistoryInfoList.filter(history => {
    const activeDate = dayjs(history.activeDate)
    const sameMonth = dayjs(activeDate).isSame(targetDate, 'month')
    if (sameMonth) {
      const day = Number.parseInt(
        dayjs(history.activeDate + '+00:00')
          .utc()
          .local()
          .format('DD'),
        10,
      )
      dayMap.set(day, dayMap.get(day) + 1)
      return history
    }
  })

  result.count = forDay.length
  result.time = forDay.reduce(sumOfTime, 0)
  result.set = [...dayMap.values()]

  return result
}

const sumOfTime = (sum, history) => {
  return sum + history.durationSec
}
