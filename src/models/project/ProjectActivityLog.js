import Model from '@/models/Model'

export default class ProjectActivityLog extends Model {
  /**
   * 프로젝트 활동 이력 구조
   * @param {Object} json
   */
  constructor(json) {
    super()
    this.uuid = json.uuid
    this.name = json.name
    this.userId = json.userId
    this.userUUID = json.userUUID
    this.userName = json.userName
    this.userNickname = json.userNickname
    this.userProfileImage =
      json.userProfileImage === 'default'
        ? require('assets/images/icon/ic-user-profile.svg')
        : json.profile
    this.message = json.message
    this.createdDate = json.createdDate
  }
}

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
    value: 'DOWNLOAD',
    label: 'projects.info.activity.download',
  },
  //   {
  //     value: 'BACKUP',
  //     label: 'projects.info.activity.backup',
  //   },
]
