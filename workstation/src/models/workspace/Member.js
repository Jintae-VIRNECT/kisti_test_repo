import Model from '@/models/Model'

export default class Member extends Model {
  /**
   * 멤버 구조
   * @param {Object} json
   */
  constructor(json) {
    super()
    this.uuid = json.uuid
    this.email = json.email
    this.name = json.name
    this.nickname = json.nickName
    this.description = json.description
    this.profile = json.profile
    this.loginLock = json.loginLock
    this.userType = json.userType
    this.role = json.role
    this.createdDate = json.createdDate
    this.updatedDate = json.updatedDate
  }
}
