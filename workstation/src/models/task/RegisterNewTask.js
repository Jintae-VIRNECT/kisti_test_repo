import Model from '@/models/Model'
import dayjs from '@/plugins/dayjs'

export default class RegisterNewTask extends Model {
  /**
   * 작업 생성 폼
   * @param {string} workspaceUUID
   * @param {object} Content
   * @param {object} Task
   * @param {array} subTasks
   */
  constructor({ workspaceUUID, content, task, subTasks }) {
    super()
    this.workspaceUUID = workspaceUUID
    this.contentUUID = content.contentUUID
    this.name = content.contentName
    this.startDate = dayjs.utc(task.schedule[0])
    this.endDate = dayjs.utc(task.schedule[1])
    this.position = task.position
    this.targetType = null
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
