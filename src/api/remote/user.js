import http from 'api/gateway'

export const getUserList = async function(data) {
  const returnVal = await http('USER_LIST', data)

  return returnVal
}
