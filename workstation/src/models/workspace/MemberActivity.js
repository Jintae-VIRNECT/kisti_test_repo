import Model from '@/models/Model'

export default class MemberActivity extends Model {
  /**
   * 멤버 활동 json 구조
   * @param {Object} json
   */
  constructor(json) {
    super()
    this.workerUUID = json.workerUUID
    this.workerName = json.workerName
    this.workerProfile =
      json.workerProfile === 'default'
        ? require('assets/images/icon/ic-user-profile.svg')
        : json.workerProfile
    this.countProgressing = json.countProgressing
    this.countAssigned = json.countAssigned
    this.percent = json.percent
    this.countContent = json.countContent
    this.lastestReportedTime = json.lastestReportedTime
  }
}
