import Model from '@/models/Model'

export default class RegisterNewProcess extends Model {
  /**
   * 공정 생성 폼
   * @param {Content, [SceneGroup], Process, [SubProcess]}
   */
  constructor({ content, sceneGroups, process, subProcesses }) {
    super()
    this.contentUUID = content.id
    this.name = content.name
    this.ownerUUID = null
    this.startDate = process.startDate
    this.endDate = process.endDate
    this.position = process.position
    this.subProcessList = sceneGroups.map((sceneGroup, index) => {
      return {
        id: sceneGroup.id,
        name: sceneGroup.name,
        priority: sceneGroup.priority,
        startDate: subProcesses[index].startDate,
        endDate: subProcesses[index].endDate,
        workerUUID: subProcesses[index].workerUUID,
      }
    })
  }
}
