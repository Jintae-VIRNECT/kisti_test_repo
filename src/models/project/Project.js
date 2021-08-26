import Model from '@/models/Model'
import { app } from '@/plugins/context'

export default class Project extends Model {
  /**
   * 프로젝트 구조 (작업할 예정)
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
 * 프로젝트 타겟 타입
 */
export const targetTypes = [
  {
    value: 'ALL',
    label: 'projects.targetTypes.all',
  },
  {
    value: 'CONTENT',
    label: 'projects.targetTypes.content',
  },
  {
    value: 'TASK',
    label: 'projects.targetTypes.task',
  },
]

/**
 * 프로젝트 모드 타입
 */
export const modeTypes = [
  {
    value: 'ALL',
    label: 'projects.modeTypes.all',
  },
  {
    value: 'QR',
    label: 'projects.modeTypes.qr',
  },
  {
    value: 'VTarget',
    label: 'projects.modeTypes.vtarget',
  },
  {
    value: 'ImageTarget',
    label: 'projects.modeTypes.imageTarget',
  },
]

/**
 * 프로젝트 공유 타입
 */
export const sharedTypes = [
  {
    value: 'ALL',
    label: 'projects.sharedTypes.all',
  },
  {
    value: 'MEMBER',
    label: 'projects.sharedTypes.member',
  },
  {
    value: 'SELECTMEMBER',
    label: 'projects.sharedTypes.selectMember',
  },
  {
    value: 'UPLOADER',
    label: 'projects.sharedTypes.uploader',
  },
  {
    value: 'PRIVATE',
    label: 'projects.sharedTypes.private',
  },
]

/**
 * 프로젝트 편집 타입
 */
export const editTypes = [
  {
    value: 'ALL',
    label: 'projects.editTypes.all',
  },
  {
    value: 'MEMBER',
    label: 'projects.editTypes.member',
  },
  {
    value: 'SELECTMEMBER',
    label: 'projects.editTypes.selectMember',
  },
  {
    value: 'UPLOADER',
    label: 'projects.editTypes.uploader',
  },
  {
    value: 'MANAGER',
    label: 'projects.editTypes.manager',
  },
]

/**
 * 프로젝트 활동정보 타입
 */
export const activityTypes = [
  {
    value: 'UPDATE',
    label: 'projects.info.activity.update',
  },
  {
    value: 'UPLOAD',
    label: 'projects.info.activity.upload',
  },
  {
    value: 'EDIT',
    label: 'projects.info.activity.edit',
  },
  {
    value: 'SHARED',
    label: 'projects.info.activity.shared',
  },
  {
    value: 'TRASH',
    label: 'projects.info.activity.moveTrash',
  },
  {
    value: 'DELETE',
    label: 'projects.info.activity.delete',
  },
  {
    value: 'BACKUP',
    label: 'projects.info.activity.backup',
  },
  {
    value: 'DOWNLOAD',
    label: 'projects.info.activity.download',
  },
]
