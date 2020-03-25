import Model from '@/models/Model'

export default class Issue extends Model {
  /**
   * 이슈 구조
   * @param {Object} json
   */
  constructor(json) {
    super()
    this.id = json.id || json.issueId
    this.photoFilePath = json.photoFilePath
    this.caption = json.caption
    this.processId = json.processId
    this.subProcessId = json.subProcessId
    this.jobId = json.jobId
    this.reportedDate = json.reportedDate
    this.processName = json.processName
    this.subProcessName = json.subProcessName
    this.jobName = json.jobName
    this.workerName = json.workerName
    this.workerProfile = json.workerProfile
    this.workerUUID = json.workerUUID
  }
}

/**
 * 이슈 검색 필터설정
 */
export const filter = {
  value: ['ALL'],
  options: [
    {
      value: 'ALL',
      label: 'SearchTabNav.filter.all',
    },
    {
      value: 'OUT',
      label: 'SearchTabNav.filter.globalIssue',
    },
    {
      value: 'IN',
      label: 'SearchTabNav.filter.workIssue',
    },
  ],
}

/**
 * 이슈 검색 정렬설정
 */
export const sort = {
  value: 'updated_at,desc',
  options: [
    {
      value: 'updated_at,desc',
      label: 'SearchTabNav.sort.createdDesc',
    },
    {
      value: 'updated_at,asc',
      label: 'SearchTabNav.sort.createdAsc',
    },
  ],
}
