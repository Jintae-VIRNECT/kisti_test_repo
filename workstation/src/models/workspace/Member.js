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
      label: 'MASTER',
    },
    {
      value: 'MANAGER',
      label: 'MANAGER',
    },
    {
      value: 'MEMBER',
      label: 'MEMBER',
    },
  ],
}

/**
 * 공정 검색 필터설정
 */
export const filter = {
  value: ['ALL'],
  options: [
    { value: 'ALL', label: 'SearchTabNav.filter.all' },
    ...role.options,
  ],
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
