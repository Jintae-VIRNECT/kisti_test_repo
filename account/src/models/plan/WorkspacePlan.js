import Model from '@/models/Model'

export default class WorkspacePlan extends Model {
  constructor() {
    super()
    this.name = 'Make'
    this.grade = 'BASIC'
    this.member = '버넥트'
    this.buyDate = '20.12.20'
    this.expireDate = '21.12.30'
  }
}
