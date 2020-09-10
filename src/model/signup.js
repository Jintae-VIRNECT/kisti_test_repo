export default class Signup {
	constructor(
		email,
		password,
		firstName,
		lastName,
		birth,
		marketInfoReceive,
		joinInfo,
		serviceInfo,
		sessionCode,
		description,
		nickname,
		mobile,
		profile,
		recoveryEmail,
	) {
		this.email = email
		this.password = password
		this.firstName = firstName
		this.lastName = lastName
		this.birth = birth
		this.marketInfoReceive = marketInfoReceive
		this.joinInfo = joinInfo
		this.serviceInfo = serviceInfo
		this.sessionCode = sessionCode
		this.description = description
		this.nickname = nickname
		this.mobile = mobile
		this.profile = profile
		this.recoveryEmail = recoveryEmail
	}
}
