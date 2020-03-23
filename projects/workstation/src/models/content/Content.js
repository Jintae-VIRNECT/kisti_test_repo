export default class Content {
  /**
   * 콘텐츠 구조
   * @param {Object} json
   */
  constructor(json) {
    this.id = json.contentUUID
    this.name = json.contentName
    this.sceneGroupTotal = json.sceneGroupTotal
    this.uploaderUUID = json.uploaderUUID
    this.status = json.status
    this.processId = json.processId
    this.createdDate = json.createdDate
  }
}

/**
 * 콘텐츠 검색 필터설정
 */
export const filter = {
  value: ['ALL'],
  options: [
    {
      value: 'ALL',
      label: 'SearchTabNav.filter.all',
    },
    {
      value: 'MANAGED',
      label: 'SearchTabNav.filter.processPublished',
    },
    {
      value: 'WAIT',
      label: 'SearchTabNav.filter.processUnpublished',
    },
  ],
}

/**
 * 콘텐츠 검색 정렬설정
 */
export const sort = {
  value: 'createdDate,desc',
  options: [
    {
      value: 'createdDate,desc',
      label: 'SearchTabNav.sort.createdDesc',
    },
    {
      value: 'createdDate,asc',
      label: 'SearchTabNav.sort.createdAsc',
    },
  ],
}
