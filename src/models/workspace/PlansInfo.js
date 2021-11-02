import Model from '@/models/Model'
import plans from '@/models/workspace/plans'
import filters from '@/mixins/filters'

const { mb2gb } = filters.filters

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

/**
 * 퍼센트 계산
 */
function rate(now, max) {
  const rate = now / max
  if (isNaN(rate)) return 0
  else if (rate === Infinity) return 100
  else return rate * 100
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
      remain: mb2gb(json.maxStorageSize - json.currentUsageStorage),
      percent: rate(json.currentUsageStorage, json.maxStorageSize),
    }
    this.project = {
      current: mb2gb(json.currentProjectStorageUsage),
      percent: rate(json.currentProjectStorageUsage, json.maxStorageSize),
    }
    this.viewCount = {
      current: json.currentUsageDownloadHit,
      default: json.defaultDownloadHit,
      add: json.addDownloadHit,
      max: json.maxDownloadHit,
      remain: json.maxDownloadHit - json.currentUsageDownloadHit,
      percent: rate(json.currentUsageDownloadHit, json.maxDownloadHit),
    }
    this.callTime = {
      current: json.currentUsageCallTime,
      default: json.defaultCallTime,
      add: json.addCallTime,
      max: json.maxCallTime,
      remain: json.maxCallTime - json.currentUsageCallTime,
      percent: rate(json.currentUsageCallTime, json.maxCallTime),
    }
    this.endDate = json.endDate
    this.planStatus = json.planStatus
    this.products = [
      new Plan(plans.remote),
      new Plan(plans.make),
      new Plan(plans.view),
    ]
    json.licenseProductInfoList.forEach(license => {
      const product = this.products.find(
        p => p.value.toLowerCase() === license.productName.toLowerCase(),
      )
      product.amount = license.quantity
      product.usedAmount = license.useLicenseAmount
      product.unUsedAmount = license.unUseLicenseAmount
    })

    this.remote = this.products.find(p => p.value === 'remote')
    this.make = this.products.find(p => p.value === 'make')
    this.view = this.products.find(p => p.value === 'view')
  }
}
