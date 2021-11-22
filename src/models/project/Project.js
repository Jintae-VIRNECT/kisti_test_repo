import Model from '@/models/Model'
import Properties from '@/models/project/Properties'

export default class Project extends Model {
  /**
   * 프로젝트 구조
   * @param {Object} json
   */
  constructor(json) {
    super()
    this.uuid = json.uuid
    this.name = json.name
    this.path = json.path
    this.size = json.size
    this.property = [
      {
        id: 1,
        label: json.property.propertyName,
        children: new Properties(json.property).tree(),
      },
    ]
    this.workspaceUUID = json.workspaceUUID
    this.uploaderUUID = json.uploaderUUID
    this.uploaderName = json.uploaderName
    this.uploaderProfile =
      json.uploaderProfile === 'default'
        ? require('assets/images/icon/ic-user-profile.svg')
        : json.uploaderProfile
    this.modeList = json.modeList
    this.editPermission = json.editPermission
    this.editUserList = json.editUserList
    this.sharePermission = json.sharePermission
    this.sharedUserList = json.sharedUserList
    this.propertySceneGroupTotal = json.propertySceneGroupTotal
    this.propertySceneTotal = json.propertySceneTotal
    this.propertyObjectTotal = json.propertyObjectTotal
    this.targetInfo = json.targetInfo
    this.targetType = this.targetInfo.type
    this.createdDate = json.createdDate
    this.updatedDate = json.updatedDate
  }
}

/**
 * 프로젝트 타겟 필터
 */
export const targetFilter = {
  value: ['ALL'],
  options: [
    {
      value: 'ALL',
      label: 'projects.targetTypes.all',
    },
    {
      value: 'QR',
      label: 'projects.targetTypes.qr',
    },
    {
      value: 'VTarget',
      label: 'projects.targetTypes.vtarget',
    },
    {
      value: 'Image',
      label: 'projects.targetTypes.imageTarget',
    },
    {
      value: 'VR',
      label: 'projects.targetTypes.vr',
    },
  ],
}

/**
 * 프로젝트 모드 필터
 */
export const modeFilter = {
  value: ['ALL'],
  options: [
    {
      value: 'ALL',
      label: 'projects.modeTypes.all',
    },
    {
      value: 'TWO_OR_THREE_DIMENSINAL',
      label: 'projects.modeTypes.twoOrThreeDimensinal',
    },
    {
      value: 'THREE_DIMENSINAL',
      label: 'projects.modeTypes.threeDimensinal',
    },
    {
      value: 'TWO_DIMENSINAL',
      label: 'projects.modeTypes.twoDimensinal',
    },
  ],
}

/**
 * 프로젝트 공유/편집 필터
 */
export const memberRoleFilter = {
  value: ['ALL'],
  options: [
    {
      value: 'ALL',
      label: 'projects.sharedTypes.all',
    },
    {
      value: 'MEMBER',
      label: 'projects.sharedTypes.member',
    },
    {
      value: 'SPECIFIC_MEMBER',
      label: 'projects.sharedTypes.selectMember',
    },
    {
      value: 'UPLOADER',
      label: 'projects.sharedTypes.uploader',
    },
    {
      value: 'MANAGER',
      label: 'projects.sharedTypes.private',
    },
  ],
}

// 콘텐츠, 타겟, 공유/편집 필터정보 리스트
export const projectFilterList = [
  {
    id: 'targetFilter',
    label: 'searchbar.filter.target',
    type: targetFilter,
  },
  { id: 'modeFilter', label: 'searchbar.filter.mode', type: modeFilter },
  {
    id: 'sharedTypes',
    label: 'searchbar.filter.share',
    type: Object.assign({}, memberRoleFilter),
  },
  {
    id: 'editTypes',
    label: 'searchbar.filter.edit',
    type: Object.assign({}, memberRoleFilter),
  },
]
