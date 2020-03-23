import Model from '@/models/Model'

export default class EditProcessRequest extends Model {
  /**
   * 공정 생성 폼
   * @param {Process, [SubProcess]}
   */
  constructor({ process, subProcesses }) {
    super()
    this.processId = process.id
    this.ownerUUID = process.ownerUUID
    this.startDate = process.startDate
    this.endDate = process.endDate
    this.position = process.position
    this.subProcessList = subProcesses.map(subProcess => {
      return {
        subProcessId: subProcess.id,
        startDate: subProcess.startDate,
        endDate: subProcess.endDate,
        workerUUID: subProcess.workerUUID,
      }
    })
  }
}
