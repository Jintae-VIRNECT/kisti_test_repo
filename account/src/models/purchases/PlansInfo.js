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
    this.storage = mb2gb(json.currentUsageStorage)
    this.viewCount = json.currentUsageDownloadHit
    this.callTime = json.currentUsageCallTime
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
