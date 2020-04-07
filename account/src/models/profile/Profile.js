import Model from '@/models/Model'

export default class Profile extends Model {
  constructor(json) {
    super()
    this.lastName = json.name
    this.firstName = json.name
    this.image = json.profile
    this.nickname = json.nickname
    this.email = json.email
    this.birth = json.birth
    this.contact = json.mobile
  }
}
