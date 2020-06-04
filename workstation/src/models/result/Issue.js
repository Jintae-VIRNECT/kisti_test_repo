import Model from '@/models/Model'

export default class Issue extends Model {
  /**
   * 이슈 구조
   * @param {Object} json
   */
  constructor(json = {}) {
    super()
    this.issueId = json.issueId
    this.photoFilePath = json.photoFilePath
    this.caption = json.caption
    this.taskId = json.taskId
    this.subTaskId = json.subTaskId
    this.stepId = json.stepId
    this.reportedDate = json.reportedDate
    this.taskName = json.taskName
    this.subTaskName = json.subTaskName
    this.stepName = json.stepName
    this.workerUUID = json.workerUUID
    this.workerName = json.workerName
    this.workerProfile = json.workerProfile === 'default' ? require('assets/images/icon/ic-user-profile.svg') : json.workerProfile 
  }
}
