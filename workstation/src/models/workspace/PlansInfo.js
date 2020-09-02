import Model from '@/models/Model'
import filters from '@/mixins/filters'

const { mb2gb } = filters.filters

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
      used: mb2gb(json.currentUsageStorage),
      max: mb2gb(json.maxStorageSize),
      remain: mb2gb(json.maxStorageSize - json.currentUsageStorage),
      percent: rate(json.currentUsageStorage, json.maxStorageSize),
    }
    this.viewCount = {
      used: json.currentUsageDownloadHit.toLocaleString(),
      max: json.maxDownloadHit.toLocaleString(),
      remain: (
        json.maxDownloadHit - json.currentUsageDownloadHit
      ).toLocaleString(),
      percent: rate(json.currentUsageDownloadHit, json.maxDownloadHit),
    }
    this.callTime = {
      used: json.currentUsageCallTime.toLocaleString(),
      max: json.maxCallTime.toLocaleString(),
      remain: (json.maxCallTime - json.currentUsageCallTime).toLocaleString(),
      percent: rate(json.currentUsageCallTime, json.maxCallTime),
    }
    this.planStatus = json.planStatus
    this.remote = json.licenseProductInfoList.find(
      license => license.productId === 1001,
    )
    this.make = json.licenseProductInfoList.find(
      license => license.productId === 1002,
    )
    this.view = json.licenseProductInfoList.find(
      license => license.productId === 1003,
    )
  }
}
