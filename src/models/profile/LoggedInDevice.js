import Model from '@/models/Model'

export default class LoggedInDevice extends Model {
  constructor(json) {
    super()
    this.ip = json.ip
    this.device = json.device
    this.location = json.location
    this.loginDate = json.lastLoggedIn
  }
}
