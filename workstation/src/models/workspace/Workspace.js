import Model from '@/models/Model'

export default class Workspace extends Model {
  /**
   * 워크스페이스 구조
   * @param {Object} json
   */
  constructor(json = {}) {
    super()
    this.uuid = json.uuid
    this.masterUserId = json.masterUserId
    this.pinNumber = json.pinNumber
    this.name = json.name
    this.description = json.description
    this.profile = json.profile
      ? json.profile
      : require('assets/images/workspace-profile.png')
    this.masterName = json.masterName
    this.masterProfile =
      json.masterProfile === 'default'
        ? require('assets/images/icon/ic-user-profile.svg')
        : json.masterProfile
    this.role = json.role
    this.updatedDate = json.updatedDate
    this.createdDate = json.createdDate
  }
}
