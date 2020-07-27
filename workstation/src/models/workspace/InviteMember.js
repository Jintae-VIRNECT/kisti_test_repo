import Model from '@/models/Model'

export default class InviteMember extends Model {
  /**
   * 멤버 초대 json 구조
   * @param {Object} json
   */
  constructor() {
    super()
    this.email = ''
    this.role = 'MEMBER'
    this.planRemote = false
    this.planMake = false
    this.planView = false
  }
}
