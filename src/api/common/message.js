import http from 'api/gateway'

export const sendPush = async function(pushSendRequest) {
  const returnVal = await http('SEND_PUSH', pushSendRequest)
  return returnVal
}
