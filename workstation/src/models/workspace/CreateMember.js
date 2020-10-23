import Model from '@/models/Model'

export default class CreateMember extends Model {
  /**
   * onpremise 멤버 생성 json 구조
   * @param {Object} json
   */
  constructor() {
    super()
    this.id = ''
    this.password = ''
    this.role = 'MEMBER'
    this.planRemote = false
    this.planMake = false
    this.planView = false
  }
}
