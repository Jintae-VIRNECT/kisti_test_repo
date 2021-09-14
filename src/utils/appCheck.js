import { getAllAppList, getLatestAppInfo } from 'api/http/download'

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

    const appList = await getAllAppList()
    const aosApp = appList.appInfoList.find(app => {
      return app.uuid === aosAppInfo.uuid
    })
    return aosApp
  } catch (err) {
    console.error(err)
    return false
  }
}
