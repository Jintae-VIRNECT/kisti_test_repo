import Model from '@/models/Model'
import ReportItem from '@/models/job/ReportItem'

export default class Report extends Model {
  /**
   * 리포트 구조
   * @param {Object} json
   */
  constructor(json) {
    super()
    this.id = json.id || json.reportId
    this.processId = json.processId
    this.subProcessId = json.subProcessId
    this.jobId = json.jobId
    this.reportedDate = json.reportedDate
    this.processName = json.processName
    this.subProcessName = json.subProcessName
    this.jobName = json.jobName
    this.workerUUID = json.workerUUID
    this.reportItem =
      json.reportItem &&
      json.reportItem.map(reportItem => new ReportItem(reportItem))
  }
}
