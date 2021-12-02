import Center from 'pages/login/LoginCenter'
import Login from 'pages/login/pages/Login'
import Terms from 'pages/login/pages/Terms'
import Signup from 'pages/login/pages/Signup'
import User from 'pages/login/pages/User'
import Find from 'pages/login/pages/Find'
import ResetPass from 'pages/login/pages/onpremise/ResetPassword'
import Complete from 'pages/login/pages/Complete'

export default {
  path: '',
  component: Center,
  children: [
    {
      path: '',
      name: 'login',
      component: Login,
    },
    {
      path: '/terms',
      name: 'terms',
      component: Terms,
    },
    {
      path: '/signup',
      name: 'signup',
      component: Signup,
      props: true,
    },
    {
      path: '/user',
      name: 'user',
      component: User,
      props: true,
    },
    {
      path: '/find',
      name: 'find',
      component: Find,
      props: true,
    },
    {
      path: '/reset_password',
      name: 'reset_password',
      component: ResetPass,
      props: true,
    },
    {
      path: '/find/:findCategory',
      name: 'findTab',
      component: Find,
      props: true,
    },
    {
      path: '/complete',
      name: 'complete',
      component: Complete,
    },
  ],
}
