import Model from '@/models/Model'

export default class UsedPlan extends Model {
  constructor() {
    super()
    this.name = 'Remote'
    this.grade = 'PRO'
    this.workspace = `VIRNECT's workspace`
    this.workspaceMaster = '버넥트'
    this.startDate = '20.12.30'
    this.expireDate = '21.12.30'
  }
}
