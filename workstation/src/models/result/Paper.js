import Model from '@/models/Model'

export default class Paper extends Model {
  /**
   * 페이퍼 구조
   * @param {Object} json
   */
  constructor(json = {}) {
    super()
    this.taskId = json.taskId
    this.subTaskId = json.subTaskId
    this.stepId = json.stepId
    this.paperId = json.paperId
    this.reportedDate = json.reportedDate
    this.taskName = json.taskName
    this.subTaskName = json.subTaskName
    this.stepName = json.stepName
    this.workerUUID = json.workerUUID
    this.workerName = json.workerName
    this.workerProfile = json.workerProfile
    this.paperActions = json.paperActions
  }
}
