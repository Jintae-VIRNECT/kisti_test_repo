import http from 'api/gateway'

export const getAccount = async function(data) {
  const returnVal = await http('LOGIN', data)
  return returnVal
}
