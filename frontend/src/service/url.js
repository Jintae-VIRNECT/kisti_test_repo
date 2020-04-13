const url = {
	auth: {
		login: '/auth/signin',
		logout: '/auth/signout',
		signup: '/auth/signup',
		emailAuth: '/auth/email',
		verification: '/auth/verification',
	},
	user: {
		login: '/users/login',
		register: '/users/register',
		registerDetail: '/users/register/detail',
		emailAuth: '/users/email',
		userInfo: '/users/{userID}',
		invite: '/users/invite',
	},
}

export default url
