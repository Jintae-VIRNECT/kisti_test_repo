import http from 'api/gateway'

/** 로컬 로그인 페이지 */
export const login = async function(data) {
  const returnVal = await http('LOGIN', data)
  return returnVal
}

/**
 * 내 정보 조회
 */
export const getAccount = async function() {
  const returnVal = await http('ACCOUNT')
  return returnVal
}

/**
 * 토큰 갱신
 * @param {String} accessToken
 * @param {String} refreshToken
 */
export const tokenRequest = async function({ accessToken, refreshToken }) {
  const returnVal = await http('TOKEN', { accessToken, refreshToken })
  return returnVal
}

/**
 * 사용자 정보 조회
 * @param {String} userId
 */
export const getUserInfo = async function({ userId }) {
  const returnVal = await http('USER_INFO', { userId })
  return returnVal
}

/**
 * 서비스 라이선스 유효성을 확인하는 서비스
 * @param {String} userId 라이센스를 확인하려는 계정 uuid
 */
export const getLicense = async function({ userId }) {
  const result = await http('GET_LICENSE', { userId })
  return result
}

/**
 * 라이선스 체크
 * @param {String} workspaceId
 * @param {String} userId
 */
export const workspaceLicense = async function({ workspaceId, userId }) {
  const result = await http('CHECK_LICENSE', { workspaceId, userId })
  return result
}

/**
 * 회사 정보
 * @param {String} userId
 * @param {String} workspaceId
 */
export const getCompanyInfo = async function({ userId, workspaceId }) {
  const returnVal = await http('COMPANY_INFO', {
    userId,
    workspaceId,
  })
  return returnVal
}

/**
 * 커스텀 설정 조회 (onpremise only)
 */
export const getSettingInfo = async function() {
  const returnVal = await http('SETTING_INFO')
  return returnVal
}
