// import { getHistoryList } from 'api/http/history'
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

/**
 * 개인 일별 데이터
 * @param {*} param0
 */
// const getMyDailyData = async ({ workspaceId, userId, date }) => {
//   const result = {}
//   const timeMap = new Map()

//   hourLabels.forEach(hour => {
//     timeMap.set(hour, 0)
//   })

//   console.log(workspaceId, userId, date)
//   const targetDate = dayjs(date)
//   const datas = await getHistoryList({
//     page: 0,
//     paging: false,
//     size: 1,
//     sort: 'createdDate,desc',
//     userId: userId,
//     workspaceId: workspaceId,
//   })

//   const forHour = datas.roomHistoryInfoList.filter(history => {
//     const activeDate = dayjs(history.activeDate)
//     const sameDay = dayjs(activeDate).isSame(targetDate, 'day')
//     if (sameDay) {
//       const hour = dayjs(history.activeDate + '+00:00')
//         .utc()
//         .local()
//         .hour()
//       timeMap.set(
//         hour >= 10 ? hour.toString() : '0' + hour.toString(),
//         timeMap.get(hour >= 10 ? hour.toString() : '0' + hour.toString()) + 1,
//       )
//       return history
//     }
//   })
//   //일일협업 개인 협업 내역 차트 데이터
//   result.count = forHour.length
//   result.time = forHour.reduce(sumOfTime, 0)
//   result.set = [...timeMap.values()]

//   return result
// }

/**
 * 개인 월별 데이터
 * @param {*} param0
 */
// const getMyMonthlyData = async ({ workspaceId, userId, date }) => {
//   console.log({ workspaceId, userId, date })

//   const result = {}

//   const targetDate = dayjs(date)
//   const datas = await getHistoryList({
//     page: 0,
//     paging: false,
//     size: 1,
//     sort: 'createdDate,desc',
//     userId: userId,
//     workspaceId: workspaceId,
//   })

//   const dayMap = new Map()
//   const days = getDays(date)
//   days.forEach(day => {
//     dayMap.set(day, 0)
//   })

//   const forDay = datas.roomHistoryInfoList.filter(history => {
//     const activeDate = dayjs(history.activeDate)
//     const sameMonth = dayjs(activeDate).isSame(targetDate, 'month')
//     if (sameMonth) {
//       const day = Number.parseInt(
//         dayjs(history.activeDate + '+00:00')
//           .utc()
//           .local()
//           .format('DD'),
//         10,
//       )
//       dayMap.set(day, dayMap.get(day) + 1)
//       return history
//     }
//   })

//   result.count = forDay.length
//   result.time = forDay.reduce(sumOfTime, 0)
//   result.set = [...dayMap.values()]

//   return result
// }

// const sumOfTime = (sum, history) => {
//   return sum + history.durationSec
// }
const sum = (sum, elm) => {
  return sum + elm
}
