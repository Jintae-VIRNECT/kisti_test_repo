export default class Member {
  /**
   * 멤버 구조
   * @param {Object} json
   */
  constructor(json) {
    this.id = json.uuid
    this.email = json.email
    this.name = json.name
    this.image = json.profile
    this.role = json.role
  }
}
