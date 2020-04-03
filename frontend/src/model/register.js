export default class Register {
  constructor(email, password, name, birth, registerInfo, registerInfoETC, serviceInfo, serviceInfoETC, session) {
    this.email = email
    this.password = password
    this.name = name
    this.birth = birth
    this.registerInfo = registerInfo
    this.registerInfoETC = registerInfoETC
    this.serviceInfo = serviceInfo
    this.serviceInfoETC = serviceInfoETC
    this.session = session
  }
}
