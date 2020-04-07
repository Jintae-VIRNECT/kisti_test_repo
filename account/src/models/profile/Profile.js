import Model from '@/models/Model'

export default class Profile extends Model {
  constructor(json) {
    super()
    this.uuid = json.uuid
    this.lastName = json.lastName
    this.firstName = json.firstName
    this.image = json.profile
    this.nickname = json.nickname
    this.email = json.email
    this.birth = json.birth
    this.contact = json.mobile
    this.qrCode = json.qrCode
    this.userType = json.userType
  }
}
