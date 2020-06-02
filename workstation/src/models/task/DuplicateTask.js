import Model from '@/models/Model'
import dayjs from '@/plugins/dayjs'

export default class DuplicateTask extends Model {
  /**
   * 작업 추가생성 폼
   * @param {string} workspaceUUID
   * @param {object} originTask
   * @param {array} sceneGroups
   * @param {object} Task
   * @param {array} subTasks
   */
  constructor({ workspaceUUID, originTask, sceneGroups, task, subTasks }) {
    super()
    console.log(sceneGroups)
    this.taskId = originTask.id
    this.workspaceUUID = workspaceUUID
    this.ownerUUID = originTask.contentManagerUUID
    this.contentUUID = originTask.contentUUID
    this.name = originTask.name
    this.startDate = dayjs.utc(task.schedule[0])
    this.endDate = dayjs.utc(task.schedule[1])
    this.position = task.position
    this.targetType = 'QR'
    this.targetSetting = task.targetSetting
    this.subTaskList = subTasks.map(subTask => {
      return {
        id: subTask.id,
        name: subTask.name,
        startDate: dayjs.utc(subTask.schedule[0]),
        endDate: dayjs.utc(subTask.schedule[1]),
        workerUUID: subTask.worker,
      }
    })
  }
}
