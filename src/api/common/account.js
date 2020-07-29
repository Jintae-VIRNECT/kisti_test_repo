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
