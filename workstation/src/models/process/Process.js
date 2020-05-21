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
  options: [{ value: 'ALL', label: 'SearchTabNav.filter.all' }, ...conditions],
}

/**
 * 공정 검색 정렬설정
 */
export const sort = {
  value: 'updated_at,desc',
  options: [
    {
      value: 'name,asc',
      label: 'SearchTabNav.sort.alphabetAsc',
    },
    {
      value: 'name,desc',
      label: 'SearchTabNav.sort.alphabetDesc',
    },
    {
      value: 'updated_at,desc',
      label: 'SearchTabNav.sort.updatedDesc',
    },
    {
      value: 'updated_at,asc',
      label: 'SearchTabNav.sort.updatedAsc',
    },
  ],
}
