import Vue from 'vue'
import VueRouter from 'vue-router'
const CallComponent = () => import('components/service/call/CallLayout')
const WorkspaceComponent = () =>
  import('components/service/workspace/WorkspaceLayout')

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
      component: CallComponent,
    },
    {
      path: '/workspace',
      name: 'workspace',
      component: WorkspaceComponent,
    },
    {
      path: '*',
      redirect: 'service',
    },
  ],
})
