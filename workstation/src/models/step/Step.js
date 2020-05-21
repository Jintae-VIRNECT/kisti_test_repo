import Model from '@/models/Model'
import { filter } from '@/models/task/Task'

export default class Step extends Model {
  /**
   * 단계 구조
   * @param {Object} json
   */
  constructor(json = {}) {
    super()
    this.id = json.id
    this.name = json.name
    this.priority = json.priority
    this.reportedDate = json.reportedDate
    this.progressRate = json.progressRate
    this.conditions = json.conditions
    this.report = json.report
    this.issue = json.issue
  }
}

/**
 * 탭
 */
export const tabs = [
  {
    name: 'allSteps',
    label: 'task.subTaskDetail.allSteps',
    filter: filter.options.map(option => option.value),
  },
  {
    name: 'waitSteps',
    label: 'task.subTaskDetail.waitSteps',
    filter: ['WAIT'],
  },
  {
    name: 'startedSteps',
    label: 'task.subTaskDetail.startedSteps',
    filter: ['UNPROGRESSING', 'PROGRESSING', 'INCOMPLETED', 'COMPLETED'],
  },
  {
    name: 'endSteps',
    label: 'task.subTaskDetail.endSteps',
    filter: ['FAILED', 'SUCCESS', 'FAULT'],
  },
]
