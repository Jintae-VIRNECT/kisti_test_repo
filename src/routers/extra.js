import Vue from 'vue'
import VueRouter from 'vue-router'
const PolicyComponent = () => import('components/policy/PolicyLayout')
const OSSComponent = () => import('components/oss/OSS')
const SupportComponent = () => import('components/support/SupportComponent')
import Terms from 'components/policy/PolicyTerms'
import Privacy from 'components/policy/PolicyPrivacy'

Vue.use(VueRouter)

export default new VueRouter({
  name: 'router',
  mode: 'history',
  routes: [
    {
      path: '/policy',
      component: PolicyComponent,
      children: [
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
        // {
        //   // 쿠키취급방침
        //   path: 'cookie',
        //   name: 'cookie',
        //   component: Cookie,
        // },
        {
          path: '*',
          redirect: { name: 'terms' },
        },
      ],
    },
    {
      path: '/OSS',
      redirect: '/OSS/mobile',
    },
    {
      path: '/OSS/:type',
      name: 'OSS',
      props: true,
      component: OSSComponent,
    },
    {
      path: '/support',
      name: 'support',
      component: SupportComponent,
    },
    {
      path: '*',
      redirect: 'privacy',
    },
  ],
})
