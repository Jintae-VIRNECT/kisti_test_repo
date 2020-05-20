import http from 'api/gateway'

export const login = async function(data) {
  const returnVal = await http('LOGIN', data)
  return returnVal
}

export const getAccount = async function(data) {
  const returnVal = await http('ACCOUNT', data)
  return returnVal
}
