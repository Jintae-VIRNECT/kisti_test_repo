import http from 'api/gateway'

/**
 * 지정일내의 개인 & 전체 협업수 반환
 * @query {String} workspaceId
 * @param {String} userId
 * @param {String} date 대상 일(2020-11-10)
 */
export const getDailyCollabo = async ({ workspaceId, userId, date }) => {
  const returnVal = await http('DAILY_COLLABO', {
    workspaceId,
    userId,
    date,
  })
  return returnVal
}

/**
 * 지정일월 내의 개인 & 전체 협업수 반환
 * @query {String} workspaceId 워크스페이스 id
 * @param {String} userId 유저 id
 * @param {String} month 대상 월 (ex : 2020-11)
 */
export const getMonthlyCollabo = async ({ workspaceId, userId, month }) => {
  const returnVal = await http('MONTHLY_COLLABO', {
    workspaceId,
    userId,
    month,
  })
  return returnVal
}
