import Model from '@/models/Model'

export default class Profile extends Model {
  constructor(json) {
    super()
    this.uuid = json.uuid
    this.lastName = json.lastName
    this.firstName = json.firstName
    this.image =
      json.profile === 'default'
        ? require('assets/images/icon/ic-user-profile.svg')
        : json.profile
    this.nickname = json.nickname
    this.email = json.email
    this.recoveryEmail = json.recoveryEmail
    this.birth = json.birth
    this.contact = json.mobile
    this.qrCode = json.qrCode
    this.userType = json.userType
    this.marketInfoReceive = json.marketInfoReceive
  }
}
