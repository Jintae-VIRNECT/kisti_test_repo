import QR from 'components/layout/qr/QR.vue'
import QRLogin from 'components/layout/qr/pages/QRLogin'
import QRLoginCenter from 'components/layout/qr/pages/QRLoginCenter'

export default {
	path: '',
	component: QR,
	children: [
		{
			path: '/qr_login',
			name: 'qr_login',
			component: QRLogin,
		},
		{
			path: '/qr_login_center',
			name: 'qr_login_center',
			component: QRLoginCenter,
		},
	],
}
