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
	}
}
