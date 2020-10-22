import Model from '@/models/Model'

export default class OnPremiseSetting extends Model {
  /**
   * onpremise 워크스페이스 세팅 구조
   * @param {Object} json
   */
  constructor(json = {}) {
    super()
    this.title = json.workspaceTitle || 'VIRNECT'
    this.logo =
      json.defaultLogo || require('assets/images/logo/logo-gnb-ci.png')
    this.favicon = json.favicon || require('assets/images/logo/favicon.png')
  }
}
