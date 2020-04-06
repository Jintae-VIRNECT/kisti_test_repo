export default class User {
	constructor(profile, name, countryCode, phoneNumber, recoveryEmail, birth) {
		this.profile = profile
		this.name = name
		this.countryCode = countryCode
		this.phoneNumber = phoneNumber
		this.recoveryEmail = recoveryEmail
		this.birth = birth
	}
}
