import Model from '@/models/Model'
import products from '@/models/products'
import filters from '@/mixins/filters'

const { mb2gb } = filters.methods

export class Plan {
  constructor(product) {
    const { id, value, label, logo } = product
    this.id = id
    this.value = value
    this.label = label
    this.logo = logo
    this.licenseType = 'BASIC'
    this.amount = 0
    this.usedAmount = 0
  }
}

export default class PlansInfo extends Model {
  /**
   * 플랜 구조
   * @param {Object} json
   */
  constructor(json) {
    super()
    this.storage = mb2gb(json.currentUsageStorage)
    this.viewCount = json.currentUsageDownloadHit
    this.callTime = json.currentUsageCallTime
    this.maxStorage = mb2gb(json.maxStorageSize)
    this.maxViewCount = json.maxDownloadHit
    this.maxCallTime = json.maxCallTime
    this.endDate = json.endDate
    this.planStatus = json.planStatus
    this.products = [
      new Plan(products.remote),
      new Plan(products.make),
      new Plan(products.view),
    ]
    json.licenseProductInfoList.forEach(license => {
      const product = this.products.find(p => p.id === license.productId)
      product.amount = license.quantity
      product.usedAmount = license.useLicenseAmount
    })
  }
}
