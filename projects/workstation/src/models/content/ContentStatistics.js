export default class ContentStatistics {
  /**
   * 콘텐츠 통계
   * @param {Object} json
   */
  constructor(json) {
    this.totalContents = json.totalContents
    this.totalManagedContents = json.totalManagedContents
  }
}
