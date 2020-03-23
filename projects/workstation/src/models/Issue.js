import Model from '@/models/Model'

export default class Issue extends Model {
  /**
   * 이슈 구조
   * @param {Object} json
   */
  constructor(json) {
    super()
    this.id = json.issueId
    this.caption = json.caption
    this.workerUUID = json.workerUUID
    this.reportedDate = json.reportedDate
  }
}
