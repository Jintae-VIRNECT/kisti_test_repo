import Model from '@/models/Model'

export default class Member extends Model {
  /**
   * 멤버 구조
   * @param {Object} json
   */
  constructor(json) {
    super()
    this.id = json.uuid
    this.email = json.email
    this.name = json.name
    this.image = json.profile
    this.role = json.role
  }
}
