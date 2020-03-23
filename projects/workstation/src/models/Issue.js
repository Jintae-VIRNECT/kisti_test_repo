export default class Issue {
  /**
   * 이슈 구조
   * @param {Object} json
   */
  constructor(json) {
    this.id = json.issueId
    this.caption = json.caption
    this.workerUUID = json.workerUUID
    this.reportedDate = json.reportedDate
  }
}
