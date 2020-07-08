import Model from '@/models/Model'
import products from '@/models/products'

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
    this.productName =
      json.productName && products[json.productName.toLowerCase()].label
    this.licenseType = json.licenseType && json.licenseType.replace(' PLAN', '')
  }
}
