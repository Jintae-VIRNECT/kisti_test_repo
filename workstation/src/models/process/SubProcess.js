import Model from '@/models/Model'

export default class SubProcess extends Model {
  /**
   * 세부공정 구조
   * @param {Object} json
   */
  constructor(json) {
    super()
    this.id = json.subProcessId
    this.name = json.name
    this.priority = json.priority
    this.jobTotal = json.jobTotal
    this.conditions = json.conditions
    this.startDate = json.startDate
    this.endDate = json.endDate
    this.progressRate = json.progressRate
    this.reportedDate = json.reportedDate
    this.isRecent = json.isRecent
    this.workerUUID = json.workerUUID
    this.issuesTotal = json.issuesTotal
    this.doneCount = json.doneCount
  }
}
