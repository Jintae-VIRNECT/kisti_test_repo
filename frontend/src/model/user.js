export default class User {
  constructor (profile, name, phoneNumber, recoveryEmail, birth) {
    this.profile = profile;
    this.name = name;
    this.phoneNumber = phoneNumber;
    this.recoveryEmail = recoveryEmail;
    this.birth = birth;
  }
}
