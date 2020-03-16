export default class Register {
  constructor(name, email, password, profile, phoneNumber, recoveryEmail, birth, registerInfo, serviceInfo, session) {
    this.name = name;
    this.email = email;
    this.password = password;
    this.profile = profile;
    this.phoneNumber = phoneNumber;
    this.recoveryEmail = recoveryEmail;
    this.birth = birth;
    this.registerInfo = registerInfo;
    this.serviceInfo = serviceInfo;
    this.session = session;
  }
}
