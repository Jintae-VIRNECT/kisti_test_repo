import http from 'api/gateway'

/**
 * 지정일내의 개인 & 전체 협업수 반환
 * @query {String} workspaceId
 * @param {String} userId
 * @param {String} date 대상 일 (ex YYYY-MM-DD)
 * @param {String} time 타임존 보정 시간 (단위 분)
 */
export const getDailyCollabo = async ({ workspaceId, userId, date, time }) => {
  const returnVal = await http('DAILY_COLLABO', {
    workspaceId,
    userId,
    date,
    time,
  })
  return returnVal
}

/**
 * 지정월 내의 개인 & 전체 협업수 반환
 * @query {String} workspaceId 워크스페이스 id
 * @param {String} userId 유저 id
 * @param {String} month 대상 월 (ex : YYYY-MM)
 * @param {String} time 타임존 보정 시간 (단위 분)
 */
export const getMonthlyCollabo = async ({
  workspaceId,
  userId,
  month,
  time,
}) => {
  const returnVal = await http('MONTHLY_COLLABO', {
    workspaceId,
    userId,
    month,
    time,
  })
  return returnVal
}
