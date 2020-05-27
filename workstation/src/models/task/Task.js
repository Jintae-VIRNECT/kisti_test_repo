import Model from '@/models/Model'

export default class Task extends Model {
  /**
   * 작업 구조
   * @param {Object} json
   */
  constructor(json = {}) {
    super()
    this.id = json.id
    this.name = json.name
    this.contentUUID = json.contentUUID
    this.position = json.position
    this.conditions = json.conditions
    this.state = json.state
    this.progressRate = json.progressRate
    this.subTaskTotal = json.subTaskTotal
    this.doneCount = json.doneCount
    this.issuesTotal = json.issuesTotal
    this.startDate = json.startDate
    this.endDate = json.endDate
    this.createdDate = json.createdDate
    this.updatedDate = json.updatedDate
    this.reportedDate = json.reportedDate
    this.subTaskAssign = json.subTaskAssign
  }
}

/**
 * 작업 상태
 */
export const conditions = [
  { value: 'WAIT', label: 'status.conditions.wait', color: 'gray' },
  {
    value: 'UNPROGRESSING',
    label: 'status.conditions.unprogressing',
    color: 'silver',
  },
  {
    value: 'PROGRESSING',
    label: 'status.conditions.progressing',
    color: 'blue',
  },
  {
    value: 'INCOMPLETED',
    label: 'status.conditions.incompleted',
    color: 'orange',
  },
  { value: 'COMPLETED', label: 'status.conditions.completed', color: 'green' },
  { value: 'FAILED', label: 'status.conditions.failed', color: 'dark-gray' },
  { value: 'SUCCESS', label: 'status.conditions.success', color: 'dark-blue' },
  { value: 'FAULT', label: 'status.conditions.fault', color: 'dark-red' },
]

/**
 * 작업 검색 필터설정
 */
export const filter = {
  value: ['ALL'],
  options: [{ value: 'ALL', label: 'searchbar.filter.all' }, ...conditions],
}

/**
 * 탭
 */
export const tabs = [
  {
    name: 'allTasks',
    label: 'task.list.allTasks',
    filter: filter.options.map(option => option.value),
  },
  {
    name: 'waitTasks',
    label: 'task.list.waitTasks',
    filter: ['WAIT'],
  },
  {
    name: 'startedTasks',
    label: 'task.list.startedTasks',
    filter: ['UNPROGRESSING', 'PROGRESSING', 'INCOMPLETED', 'COMPLETED'],
  },
  {
    name: 'endTasks',
    label: 'task.list.endTasks',
    filter: ['FAILED', 'SUCCESS', 'FAULT'],
  },
]
