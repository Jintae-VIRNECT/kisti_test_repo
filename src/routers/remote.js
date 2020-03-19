import Vue from 'vue'
import VueRouter from 'vue-router'
const ServiceComponent = () => import('components/service/ServiceLayout')
const WorkspaceComponent = () => import('components/workspace/WorkspaceLayout')

Vue.use(VueRouter)

export default new VueRouter({
  name: 'router',
  mode: 'history',
  routes: [
    {
      path: '/',
      name: 'main',
      redirect: { name: 'service' },
    },
    {
      path: '/service',
      name: 'service',
      component: ServiceComponent,
    },
    {
      path: '/workspace',
      name: 'workspace',
      component: WorkspaceComponent,
    },
    {
      path: '*',
      redirect: 'workspace',
    },
  ],
})
