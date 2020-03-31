import Model from '@/models/Model'

export default class UsedPlan extends Model {
  constructor() {
    super()
    this.name = 'Remote'
    this.grade = 'PRO'
    this.workspace = `VIRNECT's workspace`
    this.expireDate = '21.12.30'
  }
}
