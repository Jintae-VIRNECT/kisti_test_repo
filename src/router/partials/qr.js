import QR from 'pages/qr/QR.vue'
import QRLogin from 'pages/qr/pages/QRLogin'
import QRLoginCenter from 'pages/qr/pages/QRLoginCenter'

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
