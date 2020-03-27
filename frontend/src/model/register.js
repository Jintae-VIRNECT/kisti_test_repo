export default class Register {
  constructor(email, password, passwordConfirm, familyName, lastName, registerInfo, serviceInfo, session) {
    this.email = email;
    this.password = password;
    this.passwordConfirm = passwordConfirm;
    this.familyName = familyName;
    this.lastName = lastName;
    this.registerInfo = registerInfo;
    this.serviceInfo = serviceInfo;
    this.session = session;
  }
}
