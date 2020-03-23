export default class RegisterNewProcess {
  /**
   * 공정 생성 폼
   * @param {Content, SceneGroups, Process, SubProcesses}
   */
  constructor({ content, sceneGroups, process, subProcesses }) {
    this.contentUUID = content.id
    this.name = content.name
    this.ownerUUID = content.uploaderUUID
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
        workerUUID: subProcesses[index].worker,
      }
    })
  }
}
