import Model from '@/models/Model'
import SmartToolItem from '@/models/job/SmartToolItem'

export default class SmartTool extends Model {
  /**
   * 스마트툴 구조
   * @param {Object} json
   */
  constructor(json) {
    super()
    this.id = json.id || json.smartToolId
    this.processId = json.processId
    this.subProcessId = json.subProcessId
    this.jobId = json.jobId
    this.reportedDate = json.reportedDate
    this.processName = json.processName
    this.subProcessName = json.subProcessName
    this.jobName = json.jobName
    this.smartToolJobId = json.smartToolJobId
    this.smartToolWorkedCount = json.smartToolWorkedCount
    this.smartToolBatchTotal = json.smartToolBatchTotal
    this.normalToque = json.normalToque
    this.workerUUID = json.workerUUID
    this.smartToolItems =
      json.smartToolItems &&
      json.smartToolItems.map(smartToolItem => new SmartToolItem(smartToolItem))
  }
}
