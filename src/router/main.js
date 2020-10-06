import Main from 'components/layout/common/Main'
import LoginCenterRouter from './partials/login'
import QRRouter from './partials/qr'

export default {
	path: '',
	component: Main,
	redirect: {
		name: 'login',
	},
	children: [LoginCenterRouter, QRRouter],
}
