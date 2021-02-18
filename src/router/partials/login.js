import Center from 'components/layout/login/LoginCenter'
import Login from 'components/layout/login/pages/Login'
import Terms from 'components/layout/login/pages/Terms'
import Signup from 'components/layout/login/pages/Signup'
import User from 'components/layout/login/pages/User'
import Find from 'components/layout/login/pages/Find'
import ResetPass from 'components/layout/login/pages/onpremise/ResetPassword'
import Complete from 'components/layout/login/pages/Complete'

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
