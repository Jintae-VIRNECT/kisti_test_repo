export default class Register {
  constructor(email, password, name, birth, marketInfoReceive, joinInfo, serviceInfo, session) {
    this.email = email
    this.password = password
    this.name = name
    this.birth = birth
    this.marketInfoReceive = marketInfoReceive
    this.joinInfo = joinInfo
    this.serviceInfo = serviceInfo
    this.session = session
  }
}
