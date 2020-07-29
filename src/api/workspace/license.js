import http from 'api/gateway'

/**
 * 서비스 라이선스 유효성을 확인하는 서비스
 * @param {String} workspaceId 계정이 속해있는 워크스페이스 uuid
 * @param {String} userId 라이센스를 확인하려는 계정 uuid
 */
export const getLicense = async function({ userId }) {
  const result = await http('GET_LICENSE', { userId })
  return result
  // if (result.licenseInfoList) {
  //   const licenseInfo = result.licenseInfoList
  //   console.log('license information ::', licenseInfo)
  // } else {
  //   return false
  // }

  // //test code
  // //test 16 account will return false

  // if (userId === '4d127135f699616fb88e6bc4fa6d784a') {
  //   return false
  // } else {
  //   return true
  // }
}
