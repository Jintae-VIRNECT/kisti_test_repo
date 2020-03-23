import Model from '@/models/Model'

export default class Issue extends Model {
  /**
   * 이슈 구조
   * @param {Object} json
   */
  constructor(json) {
    super()
    this.id = json.id || json.issueId
    this.photoFilePath = json.photoFilePath
    this.caption = json.caption
    this.processId = json.processId
    this.subProcessId = json.subProcessId
    this.jobId = json.jobId
    this.reportedDate = json.reportedDate
    this.processName = json.processName
    this.subProcessName = json.subProcessName
    this.jobName = json.jobName
    this.workerName = json.workerName
    this.workerProfile = json.workerProfile
    this.workerUUID = json.workerUUID
  }
}
