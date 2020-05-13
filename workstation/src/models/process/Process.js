import Model from '@/models/Model'

export default class Process extends Model {
  /**
   * 공정 구조
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
    this.subProcessTotal = json.subProcessTotal
    this.doneCount = json.doneCount
    this.issuesTotal = json.issuesTotal
    this.startDate = json.startDate
    this.endDate = json.endDate
    this.createdDate = json.createdDate
    this.updatedDate = json.updatedDate
    this.subProcessAssign = json.subProcessAssign
  }
}

/**
 * 공정 상태
 */
export const conditions = [
  { value: 'WAIT', label: 'conditions.wait', color: 'gray' },
  {
    value: 'UNPROGRESSING',
    label: 'conditions.unprogressing',
    color: 'silver',
  },
  { value: 'PROGRESSING', label: 'conditions.progressing', color: 'blue' },
  { value: 'INCOMPLETED', label: 'conditions.incompleted', color: 'orange' },
  { value: 'COMPLETED', label: 'conditions.complted', color: 'green' },
  { value: 'FAILED', label: 'conditions.failed', color: 'dark-gray' },
  { value: 'SUCCESS', label: 'conditions.success', color: 'dark-blue' },
  { value: 'FAULT', label: 'conditions.fault', color: 'dark-red' },
]

/**
 * 공정 검색 필터설정
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
    name: 'allProcesses',
    label: 'process.list.allProcesses',
  },
  {
    name: 'waitProcesses',
    label: 'process.list.waitProcesses',
  },
  {
    name: 'startedProcesses',
    label: 'process.list.startedProcesses',
  },
  {
    name: 'endProcesses',
    label: 'process.list.endProcesses',
  },
]
