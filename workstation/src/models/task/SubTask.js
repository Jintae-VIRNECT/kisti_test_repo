import Model from '@/models/Model'
import { filter } from '@/models/task/Task'

export default class SubTask extends Model {
  /**
   * 하위 작업 구조
   * @param {Object} json
   */
  constructor(json = {}) {
    super()
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
