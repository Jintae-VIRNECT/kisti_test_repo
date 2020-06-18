const url = {
	auth: {
		login: '/auth/signin',
		logout: '/auth/signout',
		signup: '/auth/signup',
		emailAuth: '/auth/email',
		verification: '/auth/verification',
		qrOtp: '/auth/otp/qr',
	},
	user: {
		login: '/users/login',
		// register: '/users/register',
		// registerDetail: '/users/register/detail',
		emailAuth: '/users/email',
		userInfo: '/users/{userID}',
		invite: '/users/invite',
		findEmail: '/users/find/email',
		findPass: '/users/find/password/auth',
		changePass: '/users/find/password',
		passCodeCheck: '/users/find/password/check',
	},
}

export default url
