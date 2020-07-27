import Model from '@/models/Model'
import dayjs from '@/plugins/dayjs'

export default class EditTaskFrom extends Model {
  /**
   * 작업 편집 폼
   * @param {object} Task
   * @param {array} subTasks
   */
  constructor({ task, subTasks }) {
    super()
    this.taskId = task.id
    this.startDate = dayjs.utc(task.schedule[0])
    this.endDate = dayjs.utc(task.schedule[1])
    this.position = task.position
    this.subTaskList = subTasks.map(subTask => {
      return {
        subTaskId: subTask.id,
        name: subTask.name,
        priority: subTask.priority,
        startDate: dayjs.utc(subTask.schedule[0]),
        endDate: dayjs.utc(subTask.schedule[1]),
        workerUUID: subTask.worker,
      }
    })
  }
}
