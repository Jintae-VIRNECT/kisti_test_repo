import Model from '@/models/Model'

export default class Workspace extends Model {
  constructor() {
    super()
    this.name = `virnect's workspace`
    this.master = '버넥트'
    this.joinDate = '21.12.30'
    this.workspaceGrade = 'MANAGER'
    this.planName = 'Remote'
    this.planGrade = 'BASIC'
  }
}
