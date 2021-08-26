import Vue from 'vue'
import VueRouter from 'vue-router'
const OSSComponent = () => import('components/oss/OSS')
const SupportComponent = () => import('components/support/SupportComponent')

Vue.use(VueRouter)

export default new VueRouter({
  name: 'router',
  mode: 'history',
  routes: [
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
