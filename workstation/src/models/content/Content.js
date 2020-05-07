import Model from '@/models/Model'

export default class Content extends Model {
  /**
   * 콘텐츠 구조
   * @param {Object} json
   */
  constructor(json) {
    super()
    this.workspaceUUID = json.workspaceUUID
    this.contentUUID = json.contentUUID
    this.contentName = json.contentName
    this.shared = json.shared
    this.sceneGroupTotal = json.sceneGroupTotal
    this.contentSize = json.contentSize
    this.uploaderUUID = json.uploaderUUID
    this.uploaderName = json.uploaderName
    this.uploaderProfile = json.uploaderProfile
    this.path = json.path
    this.converted = json.converted
    this.targets = json.targets
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
      label: 'searchbar.filter.all',
    },
    {
      value: 'YES',
      label: 'searchbar.filter.processPublished',
    },
    {
      value: 'NO',
      label: 'searchbar.filter.processUnpublished',
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
      label: 'searchbar.sort.createdDesc',
    },
    {
      value: 'createdDate,asc',
      label: 'searchbar.sort.createdAsc',
    },
  ],
}
