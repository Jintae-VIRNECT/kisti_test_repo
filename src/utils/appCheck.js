import { getLatestAppInfo } from 'api/http/download'

/**
 * 최신 버전의 Remote Android App 정보 반환
 * @returns {Object}
 */
export const getLatestRemoteAosAppInfo = async () => {
  try {
    const appInfo = await getLatestAppInfo({ productName: 'remote' })
    const aosAppInfo = appInfo.appInfoList.find(info => {
      return info.deviceType === 'Mobile'
    })

    return aosAppInfo
  } catch (err) {
    console.error(err)
    return false
  }
}

/**
 * Android 앱 구동을 위한 intent 링크 반환
 * @param {Object} info {workspaceId, sessionId, packageName}
 * @returns {String} intent link string
 */
export const getIntentLink = async info => {
  const intentLink = `intent://remote?workspaceId=${info.workspaceId}&sessionId=${info.sessionId}#$d#Intent;scheme=virnect;action=android.intent.action.VIEW;category=android.intent.category.BROWSABLE;package=${info.packageName};end`
  return intentLink
}

/**
 * iOS의 version을 반환한다.
 *
 * iOS가 아니라면 0 반환
 * @returns {Number} iOS version
 *
 */
export const getIOSversion = () => {
  const agent = window.navigator.userAgent
  const start = agent.indexOf('OS ')

  const isIPhone = agent.indexOf('iPhone') > -1
  const isIpad = agent.indexOf('iPad') > -1

  if ((isIPhone || isIpad) && start > -1) {
    return Number(agent.substr(start + 3, 3).replace('_', '.'))
  }

  return 0
}
