import Model from '@/models/Model'

export default class EditSubProcessRequest extends Model {
  /**
   * 세부공정 편집 폼
   * @param {SubProcess}
   */
  constructor(subProcess) {
    super()
    this.subProcessId = subProcess.id
    this.name = subProcess.name
    this.startDate = subProcess.startDate
    this.endDate = subProcess.endDate
    this.workerUUID = subProcess.workerUUID
  }
}
