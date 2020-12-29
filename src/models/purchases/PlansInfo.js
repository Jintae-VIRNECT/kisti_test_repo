import Model from '@/models/Model'
import products from '@/models/products'
import filters from '@/mixins/filters'

const { mb2gb } = filters.methods

export class Plan {
  constructor(product) {
    const { value, label, logo } = product
    this.value = value
    this.label = label
    this.logo = logo
    this.licenseType = 'PUBLIC'
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
    this.storage = {
      current: mb2gb(json.currentUsageStorage),
      default: mb2gb(json.defaultStorageSize),
      add: mb2gb(json.addStorageSize),
      max: mb2gb(json.maxStorageSize),
    }
    this.viewCount = {
      current: json.currentUsageDownloadHit,
      default: json.defaultDownloadHit,
      add: json.addDownloadHit,
      max: json.maxDownloadHit,
    }
    this.callTime = {
      current: json.currentUsageCallTime,
      default: json.defaultCallTime,
      add: json.addCallTime,
      max: json.maxCallTime,
    }
    this.endDate = json.endDate
    this.planStatus = json.planStatus
    this.products = [
      new Plan(products.remote),
      new Plan(products.make),
      new Plan(products.view),
    ]
    json.licenseProductInfoList.forEach(license => {
      const product = this.products.find(
        p => p.value === license.productName.toLowerCase(),
      )
      product.amount = license.quantity
      product.usedAmount = license.useLicenseAmount
    })
  }
}
