import Vue from 'vue'
import VueRouter from 'vue-router'
import PolicyRouter from './partials/policy'
import MobileRouter from './partials/mobile'
const PolicyComponent = () => import('components/policy/PolicyLayout')
const MobileComponent = () => import('components/mobileApp/MobileAppLayout')
const OSSComponent = () => import('components/oss/OSS')
const SupportComponent = () => import('components/support/SupportComponent')

Vue.use(VueRouter)

export default new VueRouter({
  name: 'router',
  mode: 'history',
  routes: [
    {
      path: '/policy',
      component: PolicyComponent,
      children: PolicyRouter,
    },
    {
      path: '/m',
      component: MobileComponent,
      children: MobileRouter,
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
