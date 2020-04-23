import Model from '@/models/Model'

export default class Workspace extends Model {
  /**
   * 워크스페이스 구조
   * @param {Object} json
   */
  constructor(json = {}) {
    super()
    this.id = json.id
    this.uuid = json.uuid
    this.masterUserId = json.masterUserId
    this.masterName = json.masterName
    this.masterProfile = json.masterProfile
    this.pinNumber = json.pinNumber
    this.name = json.name
    this.description = json.description
    this.profile = json.profile
    this.role = json.role
    this.updatedDate = json.updatedDate
    this.createdDate = json.createdDate
    this.joinDate = json.joinDate
  }
}
