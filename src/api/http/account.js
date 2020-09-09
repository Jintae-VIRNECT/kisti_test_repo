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
