import Model from '@/models/Model'
import products from '@/models/products'
import filters from '@/mixins/filters'

const { mb2gb } = filters.methods

export default class PlansInfo extends Model {
  /**
   * 플랜 구조
   * @param {Object} json
   */
  constructor(json) {
    super()
    this.storage = {
      max: mb2gb(json.maxStorageSize),
      current: mb2gb(json.currentUsageStorage),
      percent: (json.currentUsageStorage / json.maxStorageSize) * 100,
    }
    this.viewCount = {
      max: json.maxDownloadHit,
      current: json.currentUsageDownloadHit,
      percent: (json.currentUsageDownloadHit / json.maxDownloadHit) * 100,
    }
    this.callTime = {
      max: json.maxCallTime,
      current: json.currentUsageCallTime,
      percent: (json.currentUsageCallTime / json.maxCallTime) * 100,
    }
    this.products = json.licenseProductInfoList.map(license => {
      const { value, label, logo } = products[license.productName.toLowerCase()]
      return {
        value,
        label,
        logo,
        licenseType: license.licenseType.replace(' PLAN', ''),
        amount: license.quantity,
        usedAmount: license.useLicenseAmount,
      }
    })
  }
}
