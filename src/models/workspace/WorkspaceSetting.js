import Model from '@/models/Model'

export default class WorkspaceSetting extends Model {
  /**
   * 워크스페이스 세팅 구조
   * @param {Object} json
   */
  constructor(json = {}) {
    super()
    this.title = json.workspaceTitle || 'VIRNECT'
    this.logo =
      json.defaultLogo || require('assets/images/logo/logo-gnb-ci.png')
    this.remoteLogo =
      json.whiteLogo || require('assets/images/logo/default_remote.svg')
    this.favicon = json.favicon || require('assets/images/logo/favicon.png')
    this.androidType1Logo =
      json.remoteAndroidSplashLogo ||
      require('assets/images/logo/android_logo_type1.svg')
    this.androidType2Logo =
      json.remoteAndroidLoginLogo ||
      require('assets/images/logo/android_logo_type2.svg')
    this.hololens2Logo =
      json.remoteHololens2CommonLogo ||
      require('assets/images/logo/hololens2_logo_type.png')
  }
}
