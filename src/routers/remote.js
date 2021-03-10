import Vue from 'vue'
import VueRouter from 'vue-router'
const ServiceComponent = () =>
  import(/* webpackChunkName: "service" */ 'components/service/ServiceLayout')
const WorkspaceComponent = () =>
  import(
    /* webpackChunkName: "workspace" */ 'components/workspace/WorkspaceLayout'
  )

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
      path: '*',
      redirect: 'workspace',
    },
  ],
})
