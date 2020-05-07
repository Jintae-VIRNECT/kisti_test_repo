import Model from '@/models/Model'

export default class Member extends Model {
  /**
   * 멤버 구조
   * @param {Object} json
   */
  constructor(json) {
    super()
    this.uuid = json.uuid
    this.email = json.email
    this.name = json.name
    this.nickname = json.nickName
    this.description = json.description
    this.profile = json.profile
    this.loginLock = json.loginLock
    this.userType = json.userType
    this.role = json.role
    this.createdDate = json.createdDate
    this.updatedDate = json.updatedDate
    this.joinDate = json.joinDate
  }
}

/**
 * 권한
 */
export const role = {
  value: 'MEMBER',
  options: [
    {
      value: 'MASTER',
      label: 'members.role.master',
    },
    {
      value: 'MANAGER',
      label: 'members.role.manager',
    },
    {
      value: 'MEMBER',
      label: 'members.role.member',
    },
  ],
}

/**
 * 공정 검색 필터설정
 */
export const filter = {
  value: ['ALL'],
  options: [{ value: 'ALL', label: 'searchbar.filter.all' }, ...role.options],
}

/**
 * 공정 검색 정렬설정
 */
export const sort = {
  value: 'role,asc',
  options: [
    {
      value: 'role,asc',
      label: 'searchbar.sort.role',
    },
    {
      value: 'nickname,asc',
      label: 'searchbar.sort.alphabetAsc',
    },
    {
      value: 'nickname,desc',
      label: 'searchbar.sort.alphabetDesc',
    },
  ],
}
