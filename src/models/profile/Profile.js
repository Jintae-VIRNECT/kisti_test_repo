import Model from '@/models/Model'
import jwtDecode from 'jwt-decode'

export default class Profile extends Model {
  constructor(json, accessToken) {
    super()
    this.userId = jwtDecode(accessToken).userId
    this.uuid = json.uuid
    this.lastName = json.lastName
    this.firstName = json.firstName
    this.profile =
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
    this.question = json.question
    this.answer = json.answer
  }
}
