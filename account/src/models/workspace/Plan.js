import Model from '@/models/Model'
import products from '@/models/products'

export default class Plan extends Model {
  /**
   * 플랜 구조
   * @param {Object} json
   */
  constructor(json = {}) {
    super()
    this.product = json.planProduct
    this.planName = products[json.planProduct.toLowerCase()].label
    this.grade = 'PUBLIC'
    this.workspaceUUID = json.workspaceId
    this.workspaceName = json.workspaceName
    this.workspaceProfile = json.workspaceProfile
      ? json.workspaceProfile
      : require('assets/images/icon/ic-user-profile.svg')
    this.renewalDate = json.renewalDate
  }
}
