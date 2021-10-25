/**
 * 다운로드 서버에 요청하는 API
 */

import http from 'api/gateway'

/**
 * remote에 배포된 최신 앱 리스트를 반환
 * @param {Object} productName 프로덕트 명(default='remote')
 * @default { productName = 'remote' }
 * @returns {Object} 앱 정보
 */
export const getLatestAppInfo = async ({ productName = 'remote' }) => {
  const returnVal = await http('LATEST_APP_INFO', { productName })
  return returnVal
}
