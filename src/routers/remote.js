import Vue from 'vue'
import VueRouter from 'vue-router'

const ServiceComponent = () =>
  import(/* webpackChunkName: "service" */ 'components/service/ServiceLayout')
const WorkspaceComponent = () =>
  import(
    /* webpackChunkName: "workspace" */ 'components/workspace/WorkspaceLayout'
  )

const QRComponent = () =>
  import(/* webpackChunkName: "qr" */ 'components/qr/QrLayout')

import SpotRouter from './spot'

Vue.use(VueRouter)

export default new VueRouter({
  name: 'router',
  mode: 'history',
  routes: [
    {
      path: '/',
      name: 'main',
      redirect: { name: 'workspace' },
    },
    {
      path: '/service',
      name: 'service',
      component: ServiceComponent,
    },
    {
      path: '/home',
      name: 'workspace',
      component: WorkspaceComponent,
    },
    {
      path: '/qr',
      name: 'qr',
      component: QRComponent,
    },
    {
      path: '*',
      redirect: 'workspace',
    },
    ...SpotRouter,
  ],
})
