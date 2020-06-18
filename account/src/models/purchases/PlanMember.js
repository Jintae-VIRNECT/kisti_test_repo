import Model from '@/models/Model'

export default class PlanMember extends Model {
  /**
   * 플랜 사용자 구조
   * @param {Object} json
   */
  constructor(json = {}) {
    super()
    this.product = 'REMOTE'
    this.grade = 'BASIC'
    this.memberUUID = 123
    this.memberName = '버넥트'
    this.memberProfile = require('assets/images/icon/ic-user-profile.svg')
  }
}
