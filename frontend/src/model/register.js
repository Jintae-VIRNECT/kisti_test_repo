export default class Register {
  constructor(email, password, passwordConfirm, familyName, lastName, registerInfo, registerInfoETC, serviceInfo, serviceInfoETC, session) {
    this.email = email;
    this.password = password;
    this.passwordConfirm = passwordConfirm;
    this.familyName = familyName;
    this.lastName = lastName;
    this.registerInfo = registerInfo;
    this.registerInfoETC = registerInfoETC;
    this.serviceInfo = serviceInfo;
    this.serviceInfoETC = serviceInfoETC;
    this.session = session;
  }
}
