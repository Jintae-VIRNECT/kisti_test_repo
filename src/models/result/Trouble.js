import Model from '@/models/Model'

export default class Trouble extends Model {
  /**
   * TM 구조
   * @param {Object} json
   */
  constructor(json = {}) {
    super()
    this.issueId = json.issueId
    this.photoFilePath = json.photoFilePath
    this.caption = json.caption
    this.reportedDate = json.reportedDate
    this.workerUUID = json.workerUUID
    this.workerName = json.workerName
    this.workerProfile = json.workerProfile === 'default' ? require('assets/images/icon/ic-user-profile.svg') : json.workerProfile 
  }
}
