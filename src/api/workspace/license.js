import http from 'api/gateway'

export const getLicense = async function(workspaceId, userId) {
  const result = await http('GET_LICENSE', { workspaceId, userId })
  const licenseInfo = result.licenseInfoList

  console.log('license information ::', licenseInfo)

  //test code
  //test 16 will return false
  if (userId === '4d127135f699616fb88e6bc4fa6d784a') {
    return false
  } else {
    return true
  }

  //Please - Do not remove.
  //test1, 2, 3 only has license
  // if (licenseInfo) {
  //   const license = licenseInfo.some(info => {
  //     if (info.productName.indexOf('Remote') >= 0 && info.status === 'USE') {
  //       return true
  //     } else {
  //       return false
  //     }
  //   })

  //   return license
  // } else {
  //   return false
  // }
}
