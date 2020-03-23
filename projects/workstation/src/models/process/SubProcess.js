export default class SubProcess {
  /**
   * 세부공정 구조
   * @param {Object} json
   */
  constructor(json) {
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
