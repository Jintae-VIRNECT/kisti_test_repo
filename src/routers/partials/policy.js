import Terms from 'components/policy/PolicyTerms'
import Privacy from 'components/policy/PolicyPrivacy'
import Cookie from 'components/policy/CookieAgree'
// import OSSComponent from 'components/OpenSourceSW'

export default [
  {
    path: '',
    redirect: { name: 'terms' },
  },
  {
    // 이용약관
    path: 'terms',
    name: 'terms',
    component: Terms,
  },
  {
    // 개인정보처리방침
    path: 'privacy',
    name: 'privacy',
    component: Privacy,
  },
  {
    // 쿠키취급방침
    path: 'cookie',
    name: 'cookie',
    component: Cookie,
  },
  {
    path: '*',
    redirect: { name: 'terms' },
  },
]
