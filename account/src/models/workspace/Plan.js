import Model from '@/models/Model'

export default class Plan extends Model {
  /**
   * 플랜 구조
   * @param {Object} json
   */
  constructor(json = {}) {
    super()
    this.planName = '플랜'
    this.product = 'REMOTE'
    this.grade = 'BASIC'
    this.workspaceUUID = 123
    this.workspaceName = '버넥트'
    this.workspaceProfile = require('assets/images/icon/ic-user-profile.svg')
    this.renewalDate = new Date()
  }
}
