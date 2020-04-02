import Model from '@/models/Model'

export default class Profile extends Model {
  constructor() {
    super()
    this.name = '장 선영'
    this.image = require('@/assets/images/img-user-profile-08.png')
    this.nickname = '버넥트 연구원'
    this.email = 'example@example.com'
    this.birth = null
    this.contact = null
  }
}
