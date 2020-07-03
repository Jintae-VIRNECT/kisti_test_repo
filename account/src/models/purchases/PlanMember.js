import Model from '@/models/Model'

export default class PlanMember extends Model {
  /**
   * 멤버 구조
   * @param {Object} json
   */
  constructor(json) {
    super()
    this.uuid = json.uuid
    this.email = json.email
    this.nickname = json.nickName
    this.profile =
      json.profile === 'default'
        ? require('assets/images/icon/ic-user-profile.svg')
        : json.profile
    this.licenseProducts = json.licenseProducts
  }
}
