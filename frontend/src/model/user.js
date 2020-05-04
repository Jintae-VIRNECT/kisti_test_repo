export default class User {
	constructor(profile, nickname, mobile, recoveryEmail, uuid) {
		this.profile = profile
		this.nickname = nickname
		this.mobile = mobile
		this.recoveryEmail = recoveryEmail
		this.uuid = uuid
	}
}
