/**
 * 공정 생성 폼
 * @param {Object} json
 */
export function RegisterNewProcess({
  content,
  sceneGroups,
  process,
  subProcesses,
} = {}) {
  return {
    contentUUID: content.id,
    name: content.name,
    ownerUUID: content.uploaderUUID,
    startDate: process.startDate,
    endDate: process.endDate,
    position: process.position,
    subProcessList: sceneGroups.map((sceneGroup, index) => {
      return {
        id: sceneGroup.id,
        name: sceneGroup.name,
        priority: sceneGroup.priority,
        startDate: subProcesses[index].startDate,
        endDate: subProcesses[index].endDate,
        workerUUID: subProcesses[index].worker,
      }
    }),
  }
}
