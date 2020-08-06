import Model from '@/models/Model'
import { app } from '@/plugins/context'

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
    this.uploaderProfile =
      json.uploaderProfile === 'default'
        ? require('assets/images/icon/ic-user-profile.svg')
        : json.uploaderProfile
    this.path = json.path
    this.converted = json.converted
    this.targets = json.targets
    this.target = json.targets.length && json.targets[0]
    this.targetSize = json.targetSize || 10
    this.targetType = this.target.type
    this.createdDate = json.createdDate
  }
}

/**
 * 컨텐츠 공유상태
 */
export const sharedStatus = [
  {
    value: 'YES',
    label: 'contents.sharedStatus.shared',
  },
  {
    value: 'NO',
    label: 'contents.sharedStatus.noShared',
  },
]

/**
 * 컨텐츠 타겟 타입
 */
export const targetTypes = [
  {
    value: 'QR',
    label: 'contents.targetTypes.qr',
  },
  {
    value: 'VTarget',
    label: 'contents.targetTypes.vtarget',
  },
]

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
