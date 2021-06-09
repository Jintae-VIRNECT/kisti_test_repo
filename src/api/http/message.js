import http from 'api/gateway'
import { KEY } from 'configs/push.config'
import Store from 'stores/remote/store'

/**
 * 푸쉬 알람 생성
 * @param {String} event push.config.EVENT
 * @param {Array} targetUserIds
 * @param {Object} contents
 */
export const sendPush = async function(event, targetUserIds, contents) {
  const params = {
    service: KEY.SERVICE_TYPE,
    workspaceId: Store.getters['workspace'].uuid,
    userId: Store.getters['account'].uuid,
    targetUserIds: targetUserIds,
    event: event,
    contents: contents,
  }
  const returnVal = await http('SEND_PUSH', params)
  return returnVal
}

export const forceLogout = async ({ workspaceId, userId, targetUserIds }) => {
  const params = {
    workspaceId,
    userId,
    targetUserIds,
  }
  const returnVal = await http('FORCE_LOGOUT', params)
  return returnVal
}
