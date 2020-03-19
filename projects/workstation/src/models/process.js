/**
 * 공정 구조
 * @param {Object} json
 */
export function Process(json) {
  return {
    id: json.id,
    name: json.name,
    position: json.position,
    conditions: json.conditions,
    subProcessTotal: json.subProcessTotal,
    doneCount: json.doneCount,
    issuesTotal: json.issuesTotal,
  }
}

/**
 * 공정 통계
 * @param {Object} json
 */
export function ProcessStatistics(json) {
  return {
    totalProcesses: json.totalProcesses,
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
