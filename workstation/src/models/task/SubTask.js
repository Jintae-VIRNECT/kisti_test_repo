import Model from '@/models/Model'
import { filter } from '@/models/task/Task'

export default class SubTask extends Model {
  /**
   * 하위 작업 구조
   * @param {Object} json
   */
  constructor(json = {}) {
    super()
    this.taskId = json.taskId
    this.taskName = json.taskName
    this.subTaskId = json.subTaskId
    this.subTaskName = json.subTaskName
    this.priority = json.priority
    this.stepTotal = json.stepTotal
    this.conditions = json.conditions
    this.startDate = json.startDate
    this.endDate = json.endDate
    this.progressRate = json.progressRate
    this.reportedDate = json.reportedDate
    this.isRecent = json.isRecent
    this.workerUUID = json.workerUUID
    this.workerName = json.workerName
    this.workerProfile =
      json.workerProfile === 'default'
        ? require('assets/images/icon/ic-user-profile.svg')
        : json.workerProfile
    this.issuesTotal = json.issuesTotal
    this.doneCount = json.doneCount
  }
}

/**
 * 탭
 */
export const tabs = [
  {
    name: 'allSubTasks',
    label: 'task.detail.allSubTasks',
    filter: filter.options.map(option => option.value),
  },
  {
    name: 'waitSubTasks',
    label: 'task.detail.waitSubTasks',
    filter: ['WAIT'],
  },
  {
    name: 'startedSubTasks',
    label: 'task.detail.startedSubTasks',
    filter: ['UNPROGRESSING', 'PROGRESSING', 'INCOMPLETED', 'COMPLETED'],
  },
  {
    name: 'endSubTasks',
    label: 'task.detail.endSubTasks',
    filter: ['FAILED', 'SUCCESS', 'FAULT'],
  },
]
