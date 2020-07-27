import Model from '@/models/Model'

export default class ContentStatistics extends Model {
  /**
   * 콘텐츠 통계
   * @param {Object} json
   */
  constructor(json) {
    super()
    this.totalContents = json.totalContents
    this.totalManagedContents = json.totalManagedContents
  }
}
